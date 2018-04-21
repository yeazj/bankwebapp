package sg.edu.sutd.bank.webapp.service;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.mockito.Mock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.ClientTransaction;
import sg.edu.sutd.bank.webapp.model.TransactionStatus;
import sg.edu.sutd.bank.webapp.model.User;
import sg.edu.sutd.bank.webapp.service.AbstractDAOImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractDAOImpl.class)
public class TestClientTransactionDAO {

    @Mock
    DataSource mockDataSource;
    @Mock
    Connection mockConn;
    @Mock
    PreparedStatement mockPreparedStmnt;
    @Mock
    ResultSet mockResultSet;
    @Mock
    ClientTransaction mockClientTransaction;
    @Mock
    User mockUser;
    
    public TestClientTransactionDAO() {
    }
	
	@Test
	public void test_create_Normal() throws Exception {
/*		
		Connection conn = connectDB();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = prepareStmt(conn, "INSERT INTO client_transaction(trans_code, amount, to_account_num, user_id, status)"
					+ " VALUES(?,?,?,?,?)");
			int idx = 1;
			ps.setString(idx++, clientTransaction.getTransCode());
			ps.setBigDecimal(idx++, clientTransaction.getAmount());
			ps.setInt(idx++, clientTransaction.getToAccountNum());
			ps.setInt(idx++, clientTransaction.getUser().getId());
			ps.setString(idx++, clientTransaction.getStatus().name());
			executeInsert(clientTransaction, ps);
		} catch (SQLException e) {
			throw ServiceException.wrap(e);
			//Added closeDB COVERITY_SCAN
		} finally {
			closeDb(conn, ps, rs);
		}
*/		
		//test data
		String testString = "testString";
		BigDecimal testBigDecimal = new BigDecimal("1.0000001");
		int testInt = 1;
		
		
		//mocking
		PowerMockito.mockStatic(AbstractDAOImpl.class);
        when(AbstractDAOImpl.connectDB()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStmnt);
        
        when(mockClientTransaction.getTransCode()).thenReturn(testString);
        when(mockClientTransaction.getAmount()).thenReturn(testBigDecimal);
        when(mockClientTransaction.getToAccountNum()).thenReturn(testInt);
        when(mockClientTransaction.getUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(testInt);
        when(mockClientTransaction.getStatus()).thenReturn(TransactionStatus.PENDING);
        
        doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
        when(mockPreparedStmnt.executeUpdate()).thenReturn(testInt);
        when(mockPreparedStmnt.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE);
        when(mockResultSet.getInt(testInt)).thenReturn(testInt);

        // initialization and invoking tested method
        ClientTransactionDAOImpl testClientTransactionDAO = new ClientTransactionDAOImpl();
        testClientTransactionDAO.create(mockClientTransaction);

		//verify and assert methods are called
        verify(mockConn, times(1)).prepareStatement(anyString(), anyInt());
        verify(mockPreparedStmnt, times(1)).setBigDecimal(anyInt(), any(BigDecimal.class));
        verify(mockPreparedStmnt, times(2)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmnt, times(2)).setString(anyInt(), anyString());
        verify(mockUser,times(1)).getId();
        verify(mockClientTransaction, times(1)).setId(anyInt());

        PowerMockito.verifyStatic(times(1));
        AbstractDAOImpl.closeDb(any(Connection.class), any(Statement.class), (ResultSet) isNull());
	}
	
	@Test
	public void test_updateAccount_Normal() throws Exception {
/*		
    	Connection conn = connectDB();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = prepareStmt(conn, "UPDATE client_account SET amount = amount + ? WHERE user_id = ?");
            int idx = 1;
            ps.setBigDecimal(idx++, clientTransaction.getAmount());
            ps.setInt(idx++, clientTransaction.getToAccountNum());
            executeUpdate(ps);
        } catch (SQLException e) {
            throw ServiceException.wrap(e);
        } finally {
			closeDb(conn, ps, rs);
		}
*/		
		//test data
		BigDecimal testBigDecimal = new BigDecimal("1.0000001");
		int testInt = 1;
		
		//mocking
		PowerMockito.mockStatic(AbstractDAOImpl.class);
        when(AbstractDAOImpl.connectDB()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStmnt);
        
        when(mockClientTransaction.getAmount()).thenReturn(testBigDecimal);
        when(mockClientTransaction.getToAccountNum()).thenReturn(testInt);
        
        doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
        when(mockPreparedStmnt.executeUpdate()).thenReturn(testInt);

        // initialization and invoking tested method
        ClientTransactionDAOImpl testClientTransactionDAO = new ClientTransactionDAOImpl();
        testClientTransactionDAO.updateAccount(mockClientTransaction);

		//verify and assert methods are called
        verify(mockConn, times(1)).prepareStatement(anyString(), anyInt());
        verify(mockPreparedStmnt, times(1)).setBigDecimal(anyInt(), any(BigDecimal.class));
        verify(mockPreparedStmnt, times(1)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmnt, times(1)).executeUpdate();

        PowerMockito.verifyStatic(times(1));
        AbstractDAOImpl.closeDb(any(Connection.class), any(Statement.class), (ResultSet) isNull());
	}
}