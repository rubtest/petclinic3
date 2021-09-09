package testresource;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.sql.SQLConnection;

public class TestRealWorldIssue35 {

	public void testSimpleSqlInjection(HttpServerRequest request) {
		// Case 1: no lambda involved
		String paramValue = request.getParam("paramName"); // 'request.getParam' is a SOURCE here
		final SQLConnection dummyConn = null;
		dummyConn.execute("create table test1(id int primary key, name varchar(255))" + paramValue, null); // True Positive
	}

	public void testLambdaSqlInjection(HttpServerRequest request) {
		// Case 2: lambda involved
		String paramValue = request.getParam("paramName"); // 'request.getParam' is a SOURCE here
		client.getConnection(conn -> {
			final SQLConnection connection = conn.result();
			connection.execute("select * from test2 where name = " + paramValue, null); // False-Negative
		});
	}

	public void testAnonymousClassSqlInjection(HttpServerRequest request) {
		// Case 3: lambda transformed into anonymous class/method and variable effectively final
		String paramValue = request.getParam("paramName"); // 'request.getParam' is a SOURCE here
		client.getConnection(new Handler<AsyncResult<SQLConnection>>() {
			@Override
			public void handle(AsyncResult<SQLConnection> event) {
				final SQLConnection connection = event.result();
				connection.execute("select * from test3 where name = " + paramValue, null); // False-Negative
			}
		});
	}

	public void testAnonymousClassSqlInjection2(HttpServerRequest request) {
		// Case 4: lambda transformed into anonymous class/method and source called in the method
		String paramValue = request.getParam("paramName"); // 'request.getParam' is a SOURCE here
		client.getConnection(new Handler<AsyncResult<SQLConnection>>() {
			@Override
			public void handle(AsyncResult<SQLConnection> event) {
				final SQLConnection connection = event.result();
				connection.execute("select * from test4 where name = " + request.getParam("paramName"), null); // True Positive
			}
		});
	}
}
