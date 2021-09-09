package testresource;

import com.atlassian.bamboo.ResultKey;
import com.atlassian.bamboo.agent.classserver.AgentServerManager;
import com.atlassian.bamboo.filter.NewRelicTransactionNamingFilter;
import com.atlassian.bamboo.security.SecureToken;
import com.atlassian.bamboo.security.SecureTokenService;
import com.atlassian.bamboo.setup.BootstrapManager;
import com.atlassian.bamboo.util.Narrow;
import com.atlassian.bamboo.v2.build.agent.messages.AuthenticableMessage;
import com.atlassian.bamboo.v2.build.agent.messages.BambooAgentMessage;
import com.atlassian.bamboo.v2.build.agent.remote.sender.HttpMessageSender;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TestRealWorldIssue34 extends HttpServlet
{

    private static final Logger log = Logger.getLogger(DeliverMessageServlet.class);

    Supplier<AgentServerManager> agentServerManager;

    Supplier<SecureTokenService> secureTokenService;

    @Override
    public void init() throws ServletException
    {
        agentServerManager = new LazyComponentReference<AgentServerManager>(AgentServerManager.BEAN_NAME);
        secureTokenService = new LazyComponentReference<SecureTokenService>(SecureTokenService.BEAN_NAME);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException
    {
        final String providedFingerprint = request.getParameter(BootstrapManager.SERVER_FINGERPRINT_PARAM);
        if (!agentServerManager.get().isServerFingerprintValid(providedFingerprint))
        {
            log.warn("Incorrect fingerprint: " + providedFingerprint + ". This could be due to a remote agent left over from a previous Bamboo server process, or an attempted attack.");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final Object object = deserializeObject(request, response);
        if (object==null)
        {
            return;
        }

        final BambooAgentMessage message;
        try
        {
            message = (BambooAgentMessage) object;
        }
        catch (final ClassCastException exception)
        {
            log.error("Object is not a BambooAgentMessage.", exception);
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, exception.toString());
            return;
        }

        deliverMessage(response, message);
    }

    void deliverMessage(final HttpServletResponse response, final BambooAgentMessage message) throws IOException
    {
        try
        {
            if (message instanceof AuthenticableMessage)
            {
                final AuthenticableMessage authenticableMessage = (AuthenticableMessage) message;
                final SecureToken authenticationToken = authenticableMessage.getAuthenticationToken();
                final ResultKey resultKey = authenticableMessage.getIdentification();
                if (!getSecureTokenService().isValid(authenticationToken, resultKey))
                {
                    final String msg = "Cannot process message: invalid authentication token [" + authenticationToken.getToken() + "] for " + authenticableMessage.getIdentification();
                    log.error(msg);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
                }
                else
                {
                    final Object messageReply = message.deliver();
                    if (messageReply == null)
                    {
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    }
                    else
                    {
                        serializeObject(messageReply, response);
                    }
                }
            }
            else
            {
                final String msg = "Cannot process message: Only AuthenticableMessage are allowed. Message of type: " + message;
                log.error(msg);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
            }
        }
        finally
        {
            cleanup(message);
        }
    }

    SecureTokenService getSecureTokenService()
    {
        return secureTokenService.get();
    }

    void serializeObject(Object object, HttpServletResponse response) throws IOException
    {
        @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
        ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
        oos.flush();
        oos.writeObject(object);
        oos.flush();
    }

    @Nullable
    private Object deserializeObject(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        Object object = null;

        final ServletInputStream inputStream = request.getInputStream();
        try
        {
            final ObjectInput objectInput = new ObjectInputStream(inputStream);
            try
            {
                object = objectInput.readObject();
            }
            catch (final IOException exception)
            {
                log.error(createErrorMessage(request), exception);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.toString());
            }
            catch (final ClassNotFoundException exception)
            {
                log.error(createErrorMessage(request), exception);
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, exception.toString());
            }
            finally
            {
                objectInput.close();
            }
        }
        finally
        {
            if (object != null)
            {
                final String simpleName = object.getClass().getSimpleName();
                NewRelicTransactionNamingFilter.setTransactionName(request, "/deliverMessage/" + simpleName);

                // Ignore nerelic if it's an artifact transfer
                if ("ArtifactPublishMessage".equals(simpleName))
                {
                    request.setAttribute("com.newrelic.agent.IGNORE", true);
                }

            }

            inputStream.close();
        }

        return object;
    }

    private String createErrorMessage(final HttpServletRequest request)
    {
        final String messageDescription = request.getHeader(HttpMessageSender.HEADER_CONTENT_DESCRIPTION);
        return "Failed to deserialise message sent to " + request.getRequestURI() + "&" + request.getQueryString() + " : " + messageDescription;
    }

    private void cleanup(BambooAgentMessage message)
    {
        Closeable closeable = Narrow.reinterpret(message, Closeable.class);
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (Exception e)
            {
                log.error(e.getMessage(), e);
            }
        }
    }
}
