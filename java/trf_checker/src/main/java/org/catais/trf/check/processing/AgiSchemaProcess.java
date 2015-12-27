package org.catais.trf.check.processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.catais.trf.check.ItfReader;

public class AgiSchemaProcess extends Process {
	static final Logger logger = LogManager.getLogger(AgiSchemaProcess.class.getName());

	public AgiSchemaProcess(HashMap<String, String> params) {
		super(params);
	}

	public void run() throws ClassNotFoundException {
		String dbschema = "so_" + fosnr + "_agi";
		
		
		String sql = new StringBuilder()
				.append("CREATE SCHEMA " + dbschema + "\n")
				.append("AUTHORIZATION " + dbusr + ";")
				.toString();
		
		
		Class.forName("org.postgresql.Driver");
		
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(dburl);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException e) {
        	logger.error(e.getMessage());
        	
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException e) {
            	logger.error(e.getMessage());
            }
        }
		
		System.out.println(sql);

	}

}
