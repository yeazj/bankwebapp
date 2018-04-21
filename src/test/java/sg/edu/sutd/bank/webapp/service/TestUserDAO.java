package sg.edu.sutd.bank.webapp.service;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.mockito.Mock;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import sg.edu.sutd.bank.webapp.model.User;
import sg.edu.sutd.bank.webapp.model.UserStatus;
import sg.edu.sutd.bank.webapp.service.AbstractDAOImpl;
import sg.edu.sutd.bank.webapp.service.UserDAOImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractDAOImpl.class)
public class TestUserDAO {

    @Mock
    DataSource mockDataSource;
    @Mock
    Connection mockConn;
    @Mock
    PreparedStatement mockPreparedStmnt;
    @Mock
    ResultSet mockResultSet;
    @Mock
    User mockUser;
    
    public TestUserDAO() {
    }
    

	@Test
	public void test_loadUser_Null() throws Exception {
/*		
		Connection conn = connectDB();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT id, user_name, status FROM user WHERE user_name=?");
			ps.setString(1, userName);
			rs = ps.executeQuery();
			if (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setUserName(rs.getString("user_name"));
				user.setStatus(rs.getString("status"));
				return user;
			}
		} catch (SQLException e) {
			throw ServiceException.wrap(e);
		} finally {
			closeDb(conn, ps, rs);
		}
		return null;
*/
		//mocking
		PowerMockito.mockStatic(AbstractDAOImpl.class);
        when(AbstractDAOImpl.connectDB()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPreparedStmnt);
        doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
        when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.FALSE);
        
        // initialization and invoking tested method
		UserDAOImpl testUserDAO = new UserDAOImpl();
		User testUser = testUserDAO.loadUser("justastring");

		//verify and assert methods are called
        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockPreparedStmnt, times(1)).setString(anyInt(), anyString());
        verify(mockPreparedStmnt, times(1)).executeQuery();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(0)).getInt(anyString());
        verify(mockResultSet, times(0)).getString(anyString());
        
        PowerMockito.verifyStatic(times(1));
        AbstractDAOImpl.closeDb(any(Connection.class), any(Statement.class), any(ResultSet.class));
        
        //asserting returned data is equal to expected data
        assertThat(testUser, is(equalTo(null)));
	}
	
	@Test
	public void test_loadUser_Normal() throws Exception {
/*		
		Connection conn = connectDB();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT id, user_name, status FROM user WHERE user_name=?");
			ps.setString(1, userName);
			rs = ps.executeQuery();
			if (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setUserName(rs.getString("user_name"));
				user.setStatus(rs.getString("status"));
				return user;
			}
		} catch (SQLException e) {
			throw ServiceException.wrap(e);
		} finally {
			closeDb(conn, ps, rs);
		}
		return null;
*/		
		
		//test data
		int test_userId = 888;
		String test_userName= "testusername888";
		String test_Status = "DECLINED";
		
		//mocking
		PowerMockito.mockStatic(AbstractDAOImpl.class);
        when(AbstractDAOImpl.connectDB()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPreparedStmnt);
        doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
        when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE);
        when(mockResultSet.getInt(anyString())).thenReturn(test_userId);
        when(mockResultSet.getString(anyString())).thenReturn(test_userName, test_Status);
        
        
        // initialization and invoking tested method
		UserDAOImpl testUserDAO = new UserDAOImpl();
		User testUser = testUserDAO.loadUser("justastring");

		//verify and assert methods are called
        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockPreparedStmnt, times(1)).setString(anyInt(), anyString());
        verify(mockPreparedStmnt, times(1)).executeQuery();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(1)).getInt(anyString());
        verify(mockResultSet, times(2)).getString(anyString());
        
        PowerMockito.verifyStatic(times(1));
        AbstractDAOImpl.closeDb(any(Connection.class), any(Statement.class), any(ResultSet.class));
        
        //asserting returned data is equal to expected data
        assertThat(testUser.getId(), is(equalTo(test_userId)));
        assertThat(testUser.getUserName(), is(equalTo(test_userName)));
        assertThat(testUser.getStatus(), is(equalTo(UserStatus.DECLINED)));
	}
	
	@Test
	public void test_create_Normal() throws Exception {
/*		
		Connection conn = connectDB();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = prepareStmt(conn, "INSERT INTO user(user_name, password) VALUES(?,?)");
			int idx = 1;
			ps.setString(idx++, user.getUserName());
			ps.setString(idx++, user.getPassword());
			executeInsert(user, ps);
		} catch (SQLException e) {
			throw ServiceException.wrap(e);
		} finally {
			closeDb(conn, ps, rs);
		}
*/		
		//test data
		String testUserName = "testusername";
		String testPassword = "testPassword";
		int testNumber5 = 5;
		int testNumber1 = 1;
		
		//mocking
		PowerMockito.mockStatic(AbstractDAOImpl.class);
        when(AbstractDAOImpl.connectDB()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStmnt);
        when(mockUser.getUserName()).thenReturn(testUserName);
        when(mockUser.getPassword()).thenReturn(testPassword);
        doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
        when(mockPreparedStmnt.executeUpdate()).thenReturn(testNumber5);
        when(mockPreparedStmnt.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE);
        when(mockResultSet.getInt(testNumber1)).thenReturn(testNumber5);

        // initialization and invoking tested method
		UserDAOImpl testUserDAO = new UserDAOImpl();
		testUserDAO.create(mockUser);

		//verify and assert methods are called
        verify(mockConn, times(1)).prepareStatement(anyString(), anyInt());
        verify(mockPreparedStmnt, times(2)).setString(anyInt(), anyString());
        verify(mockUser,times(1)).setId(anyInt());

        PowerMockito.verifyStatic(times(1));
        AbstractDAOImpl.closeDb(any(Connection.class), any(Statement.class), (ResultSet) isNull());
	}
}
