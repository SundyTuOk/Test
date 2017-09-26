package com.tu.tutest.tutest_test1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, SQLException
    {
//    	XMLConfiguration config = getConfiguration();
//    	long startNumber = config.getLong("startNumber");
//    	long endNumber = config.getLong("endNumber");
//		BufferedWriter bw = new BufferedWriter(new FileWriter("电话号码.txt"));
//		
//		for(long i=startNumber;i<=endNumber;i++){
//			bw.write(i+"\n");
//		}
//		
//		bw.flush();
//		bw.close();
    	
    	test6();
    }
    
    
    public static XMLConfiguration getConfiguration()
	{
		String filePath=System.getProperty("user.dir")+"/config/config.xml";
		File file=new File(filePath);
		if (file == null || !file.exists())
			return null;
		XMLConfiguration config = null;
		try
		{
			config = new XMLConfiguration(file);
		} catch (ConfigurationException e)
		{
			e.printStackTrace();
		}
		// 设置编码
		config.setEncoding("utf-8");

		return config;

	}
    
    private static void test6() throws SQLException, IOException
	{
		List<MeterModel> models = new ArrayList<MeterModel>();
		
		//查询所有AMMETER_ID
		String sql = "select AMMETER_ID from AMMETER";
		//查电表终止示数
		String sql1 = "";
		//查剩余电量
		String sql2 = "";//"SELECT * from AMMETERAPDATAS WHERE AMMETER_ID = 42 AND ROWNUM=1 ORDER BY VALUETIME DESC ";
		//查询总购电量
		String sql3= "";
		//查询旧表剩余量
		String sql4="";
		
		PreparedStatement ps= null;
		ResultSet rs =null;
		Connection conn=null;
		
		conn=getConnection();
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		
		while(rs.next())
		{
			//查询所有AMMETER_ID
			int ammeterID = rs.getInt("AMMETER_ID");
			
			if(ammeterID == 90) continue;
			if(ammeterID == 402) continue;
			
			MeterModel model = new MeterModel();
			model.setAmmeterID(ammeterID);
			//查电表终止示数
			sql1 = "SELECT ZVALUEZY FROM zamdatas"+ammeterID+" WHERE ROWNUM=1 ORDER BY valueTime DESC";
//			Connection conn1 = getConnection();
			PreparedStatement ps1 = conn.prepareStatement(sql1);
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			model.setTerminalNum(rs1.getDouble("ZVALUEZY"));
			release(ps1,rs1);

			//查剩余电量
			sql2 = "SELECT SYVALUE from AMMETERAPDATAS WHERE AMMETER_ID ="+ ammeterID +" AND ROWNUM=1 ORDER BY VALUETIME DESC";
//			Connection conn2 = getConnection();
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			ResultSet rs2 = ps2.executeQuery();
			if(rs2.next())
			model.setSYValue(rs2.getDouble("SYVALUE"));
			release(ps2,rs2);
			
			//查询总购电量
			sql3 = "SELECT AMMETER_ID,AMMETER_NAME, SUM(THEGROSS) AS SUM FROM APSALEINFO B WHERE AMMETER_ID="+ammeterID+" GROUP BY AMMETER_ID,AMMETER_NAME ORDER BY AMMETER_ID";
			//Connection conn3 = getConnection();
			PreparedStatement ps3 = conn.prepareStatement(sql3);
			ResultSet rs3 = ps3.executeQuery();
			if(rs3.next())
			{
				model.setGross(rs3.getDouble("SUM"));
				model.setAmmeterName(rs3.getString("AMMETER_NAME"));
			}
			release(ps3,rs3);
			
			//查询旧表剩余量
			sql4 = "SELECT OLDSY from APKAIHUINFO WHERE AMMETER_ID = "+ammeterID;
			PreparedStatement ps4 = conn.prepareStatement(sql4);
			ResultSet rs4 = ps4.executeQuery();
			if(rs4.next())
			{
				model.setOldSYValue(rs4.getDouble("OLDSY"));
			}
			release(ps4,rs4);
			
			models.add(model);
		}
		
		//写到csv文件
		File out = new File("C:\\Users\\Administrator\\Desktop\\ssdf.csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = "";
		bw.write("电表id,电表name,终止电量,剩余电量,购买量,旧表剩余量,终止+剩余,购买+旧表\n");
		for(int i=0;i<models.size();i++)
		{
			MeterModel m = models.get(i);
			line = m.getAmmeterID()+","		//电表id
					+m.getAmmeterName()+","	//电表name
					+m.getTerminalNum()+","	//终止电量
					+m.getSYValue()+","		//剩余电量
					+m.getGross()+","		//购买量
					+m.getOldSYValue()+","	//旧表剩余量
					+(m.getTerminalNum()+m.getSYValue())+","//终止+剩余
					+(m.getGross()+m.getOldSYValue())+	//购买+旧表
					"\n";//旧表剩余量
			bw.write(line);
		}
		
		
		bw.flush();
		bw.close();
		
		
		
	}
	
	
	public static Connection getConnection() throws SQLException
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		} catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn = null;
		try
		{
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:orcl", "vessjc", "sa-123456");
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	 public static void release(Connection conn,PreparedStatement ps,ResultSet rs){
	        if(rs!=null){
	            try{
	                //关闭存储查询结果的ResultSet对象
	                rs.close();
	            }catch (Exception e) {
	                e.printStackTrace();
	            }
	            rs = null;
	        }
	        if(ps!=null){
	            try{
	                //关闭负责执行SQL命令的Statement对象
	            	ps.close();
	            }catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        
	        if(conn!=null){
	            try{
	                //将Connection连接对象还给数据库连接池
	                conn.close();
	            }catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	 
	public static void release(PreparedStatement ps,ResultSet rs){
	    	 
	    	 if(ps!=null){
	    		 try{
	    			 //关闭负责执行SQL命令的Statement对象
	    			 ps.close();
	    		 }catch (Exception e) {
	    			 e.printStackTrace();
	    		 }
	    	 }
	    	 
	    	 if(rs!=null){
	    		 try{
	    			 //将Connection连接对象还给数据库连接池
	    			 rs.close();
	    		 }catch (Exception e) {
	    			 e.printStackTrace();
	    		 }
	    	 }
	   }
}
