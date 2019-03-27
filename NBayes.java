import java.io.*;
import java.lang.*;
import java.util.*;
import java.sql.*;

public class NBayes {
    String columns[], tuple[];
    HashSet<String> classes=new HashSet();
    NBayes(){
        try{
            int row_cnt=0,col_cnt=0,k,row=0,col=0,iter=0;
            String values[];
            Statement st;
            ResultSet rs;
            ResultSetMetaData rsmd;
            Connection con;
            

			org.postgresql.Driver driver=new org.postgresql.Driver();

			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/KMeans","postgres","cse");
			st=con.createStatement();

            //getting meta-data
			String sql="SELECT * FROM bayes";
			rs=st.executeQuery(sql);
            rsmd=rs.getMetaData();
			col_cnt=rsmd.getColumnCount();
            String columns[] = new String[col_cnt];
            this.columns = columns;
            for(int i=0;i<col_cnt;i++){
                columns[i] = rsmd.getColumnName(i+1);
            }
            while(rs.next()){
                classes.add(rs.getString(columns[col_cnt-1]));
                row_cnt++;
            }
            Scanner in = new Scanner(System.in);
            String tuple[] = new String[col_cnt];
            this.tuple = tuple;

            for(int i=0;i<col_cnt-1;i++){
                System.out.print("Enter value of "+columns[i]+" :");
                tuple[i] = in.nextLine();
            }
            System.out.println("tuple class: "+findClass(rs,st,col_cnt,row_cnt));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public String findClass(ResultSet rs, Statement st,int col_cnt,int row_cnt){
        Iterator <String> i = classes.iterator();
        float existing = 0, probability = 0;
        String myclass="NA";
        while(i.hasNext()){
            String a = i.next();
            probability = findProbability(a,rs,st,col_cnt,row_cnt);
            if(probability > existing){
                existing = probability;
                myclass = a;
            }
        }
        return myclass;
    }

    public float findProbability(String myclass,ResultSet rs, Statement st,int col_cnt,int row_cnt){
        float prob = 1;
        int total = 0;
        try{
            String sql = "SELECT count("+columns[col_cnt-1]+") AS c FROM bayes WHERE "+columns[col_cnt-1]+"='"+myclass+"'";
            rs = st.executeQuery(sql);
            while(rs.next()){
                total = rs.getInt("c");
            }
            prob=((float)total/(float)row_cnt);
            for(int i=0;i<tuple.length-1;i++){
                sql = "SELECT count("+columns[i]+") AS c FROM bayes WHERE "+columns[i]+"='"+tuple[i]+"' AND "+columns[col_cnt-1]+"='"+myclass+"'";
                rs = st.executeQuery(sql);
                while(rs.next()){
                    prob*=((float)rs.getInt("c")/(float)total);
                }
            }
            System.out.println("probability for "+myclass+": "+prob);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return prob;
    }
    public static void main(String args[]){
        new NBayes();
    }
}