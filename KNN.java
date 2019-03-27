import java.io.*;
import java.lang.*;
import java.util.*;
import java.sql.*;

public class KNN {
    float points[][], tuple[];
    KNN(){
        try{
            int row_cnt=0,col_cnt=0,k,row=0,col=0,iter=0;
            Statement st;
            ResultSet rs;
            ResultSetMetaData rsmd;
            Connection con;

			org.postgresql.Driver driver=new org.postgresql.Driver();

			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/KMeans","postgres","cse");
			st=con.createStatement();

            //getting meta-data
			String sql="SELECT * FROM knn";
			rs=st.executeQuery(sql);
            while(rs.next()){
                row_cnt++;
            }
			rsmd=rs.getMetaData();
			col_cnt=rsmd.getColumnCount();
            
            //declaring all arrays required
            float points[][] = new float[row_cnt][col_cnt];
            float tuple[]  = new float[col_cnt-1];
            this.points = points;
            this.tuple = tuple;
            Scanner in = new Scanner(System.in);
            System.out.print("enter k value: ");
            k = in.nextInt();

            //initialising and printing data points
            System.out.println("Data Points: ");
            rs=st.executeQuery(sql);
            while(rs.next()){
                for(int i=0;i<col_cnt-1;i++){
                    points[row][i] = rs.getFloat(""+(i+1));
                    System.out.print(points[row][i]+", ");
                }
                points[row][col_cnt-1] = (float)rs.getInt("class");
                System.out.print(points[row][col_cnt-1]);
                System.out.println();
                row+=1;
            }

            System.out.println("Enter data point to be classified: ");
            for(int i=0;i<col_cnt-1;i++){
                System.out.print("Enter dimension"+(i+1)+": ");
                tuple[i] = in.nextFloat();
            }
            System.out.println("tuple class is: "+findClass(col_cnt-1,row_cnt,k));   
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public int findClass(int col_cnt, int row_cnt, int k){
        float distance[] = new float[row_cnt];
        int myclasses[] = new int[k];
        for(int i=0;i<row_cnt;i++){
            float p1[] = points[i];
            distance[i] = dist(col_cnt, p1, tuple);
        }
        float temp[] = new float[distance.length];
        for(int i=0;i<distance.length;i++){
            temp[i] = distance[i];
        }
        Arrays.sort(temp);
        System.out.println("\nDistances of the new point from training set points: ");
        System.out.println(Arrays.toString(distance));
        HashSet<Integer> classes = new HashSet();

        for(int i=0;i<row_cnt;i++){
            classes.add((int)points[i][col_cnt]);
        }

        for(int i=0;i<k;i++){
            for(int j=0;j<distance.length;j++){
                if(distance[j] == temp[i]){
                    myclasses[i] = (int)points[j][col_cnt];
                }
            }
        }
        int max = -9999999;
        int max_c = -1;

        System.out.println("\nClass labels of k- nearest neighbours: ");
        System.out.println(Arrays.toString(myclasses));
        Iterator<Integer> i = classes.iterator();
        while(i.hasNext()){
            int a = i.next();
            int cnt = classCount(a,myclasses);
            if(max < cnt){
                max = cnt;
                max_c = a;
            }
        }
        return max_c;
    }

    public int classCount(int a, int myclasses[]){
        int cnt=0;
        for(int i=0;i<myclasses.length;i++){
            if(myclasses[i] == a)
                cnt++;
        }
        return cnt;
    }
    //find square of a number
    public float square(float a){
        return a*a;
    }

    //find distance between two points
    public float dist(int col_cnt, float p1[], float p2[]){
        float sum=0;
        for(int i=0; i< col_cnt; i++){
            sum += square(p1[i]-p2[i]);
        }
        return (float)Math.sqrt(sum);
    }

    //main function - execution begins here
    public static void main(String args[]){
        new KNN();
    }

}