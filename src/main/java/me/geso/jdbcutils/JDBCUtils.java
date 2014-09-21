package me.geso.jdbcutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Utility functions for JDBC.
 *
 */
public class JDBCUtils {

	/**
	 * Execute query with callback.
	 * 
	 * @param connection
	 * @param query
	 * @param callback
	 * @return Generated value from the callback
	 * @throws RichSQLException
	 */
	public static <R> R executeQuery(final Connection connection,
			final Query query,
			final ResultSetCallback<R> callback)
			throws RichSQLException {
		return JDBCUtils.executeQuery(connection,
				query.getSQL(), query.getParameters(),
				callback);
	}

	/**
	 * Execute query with callback.
	 * 
	 * @param connection
	 * @param sql
	 * @param params
	 * @param callback
	 * @return Generated value from the callback
	 * @throws RichSQLException
	 */
	public static <R> R executeQuery(final Connection connection,
			final String sql,
			final List<Object> params,
			final ResultSetCallback<R> callback)
			throws RichSQLException {
		try (final PreparedStatement ps = connection.prepareStatement(sql)) {
			JDBCUtils.fillPreparedStatementParams(ps, params);
			try (final ResultSet rs = ps.executeQuery()) {
				return callback.call(rs);
			}
		} catch (final SQLException ex) {
			throw new RichSQLException(ex, sql, params);
		}
	}

	/**
	 * Execute query.
	 * 
	 * @param connection
	 * @param query
	 * @return Affected rows.
	 * @throws RichSQLException
	 */
	public static int executeUpdate(final Connection connection,
			final Query query)
			throws RichSQLException {
		return JDBCUtils.executeUpdate(connection, query.getSQL(),
				query.getParameters());
	}

	/**
	 * Execute query.
	 * 
	 * @param connection
	 * @param sql
	 * @param params
	 * @return Affected rows.
	 * @throws RichSQLException
	 */
	public static int executeUpdate(final Connection connection,
			final String sql,
			final List<Object> params)
			throws RichSQLException {
		try (final PreparedStatement ps = connection.prepareStatement(sql)) {
			JDBCUtils.fillPreparedStatementParams(ps, params);
			return ps.executeUpdate();
		} catch (final SQLException ex) {
			throw new RichSQLException(ex, sql, params);
		}
	}

	/**
	 * Shorthand method.
	 * 
	 * @param connection
	 * @param sql
	 * @return
	 * @throws RichSQLException
	 */
	public static int executeUpdate(final Connection connection,
			final String sql)
			throws RichSQLException {
		return JDBCUtils
				.executeUpdate(connection, sql, Collections.emptyList());
	}

	/**
	 * Fill parameters for prepared statement.
	 * 
	 * <pre>
	 * <code>JDBCUtils.fillPreparedStatementParams(preparedStatement, ImmutableList.of(1,2,3));</code>
	 * </pre>
	 * 
	 * @param preparedStatement
	 * @param params
	 * @throws SQLException
	 */
	public static void fillPreparedStatementParams(
			final PreparedStatement preparedStatement,
			final List<Object> params) throws SQLException {
		for (int i = 0; i < params.size(); ++i) {
			preparedStatement.setObject(i + 1, params.get(i));
		}
	}
}