import java.io.*;
import java.lang.*;
import java.util.*;
import java.sql.*;

public class KMedoid {
    float cluster_c[][],points[][];
    int cluster[],centroid[], count[], prev[];
    KMedoid(){
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
			String sql="SELECT * FROM kmeans";
			rs=st.executeQuery(sql);
            while(rs.next()){
                row_cnt++;
            }
			rsmd=rs.getMetaData();
			col_cnt=rsmd.getColumnCount();
            
            //declaring all arrays required
            float points[][] = new float[row_cnt][col_cnt];
            Scanner in = new Scanner(System.in);
            System.out.print("enter k value: ");
            k = in.nextInt();
            float cluster_c[][] = new float[k][col_cnt];
            int cluster[] = new int[row_cnt];
            int prev[] = new int[row_cnt];
            int count[] = new int[k];
            int centroid[] = new int[k];

            //initialising and printing data points
            System.out.println("Data Points: ");
            rs=st.executeQuery(sql);
            while(rs.next()){
                for(int i=0;i<col_cnt;i++){
                    points[row][i] = rs.getFloat(""+(i+1));
                    System.out.print(points[row][i]+", ");
                }
                System.out.println();
                row+=1;
            }

            //k random centroids(first k data points)
            for(int i=0;i<k;i++){
                centroid[i] = i;
            }

            //iterations
            iter=1;
            makeCluster(iter,k,row_cnt,col_cnt);
            while(cluster_chk(k,row_cnt) != 1){
                iter++;
                for(int i=0;i<row_cnt;i++)
                    prev[i] = cluster[i];
                makeCluster(iter,k,row_cnt,col_cnt);   
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //find the distances of point from each centroid and assign cluster number
    public void makeCluster(int iter,int k,int row_cnt, int col_cnt){
        System.out.println("iteration "+iter+": ");
        System.out.print("clusters: ");
        for(int i=0;i<row_cnt;i++){
            float min = 99999999;
            int min_c=-1;
            float p1[] = points[i];
            for(int j=0;j<k;j++){
                float p2[] = points[centroid[j]];
                float distance = dist(col_cnt,p1,p2);
                if(min > distance)
                {
                    min = distance;
                    min_c = j;
                }
            }
            cluster[i] = min_c;
            System.out.print(cluster[i]+" ");
        }
        System.out.println("\ncluster centroids : ");
        findClusterC(k,col_cnt,row_cnt);
    }

    //find the centroids for each cluster
    public void findClusterC(int cnt, int col_cnt, int row_cnt){
        for(int k=0; k<cnt; k++){
            for(int j=0;j<col_cnt;j++){
                cluster_c[k][j] = 0;
            }
        }
        for(int k=0; k<cnt; k++){
            count[k] = 0;
            for(int i=0;i<row_cnt;i++){
                if(cluster[i] == k){
                    for(int j=0;j<col_cnt;j++)
                    {
                        cluster_c[k][j] += points[i][j];
                    }
                    count[k]+=1;
                }
            }
        }
        for(int k=0; k<cnt; k++){
            for(int j=0;j<col_cnt;j++){
                cluster_c[k][j]/=count[k];
            }
        }


        for(int j=0;j<cnt;j++){
            float min = 99999999;
            float p2[] = cluster_c[j];
            for(int i=0;i<row_cnt;i++){
                float p1[] = points[i];
                float distance = dist(col_cnt,p1,p2);
                if(min > distance)
                {
                    min = distance;
                    centroid[j] = i;
                }
            }
            System.out.println("centroid"+j+"---datapoint: "+centroid[j]+"");
        }



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

    //check clusters for 2 successive iterations
    public int cluster_chk(int k,int row_cnt){
        for(int i=0;i<row_cnt;i++)
            if(prev[i]!=cluster[i])
                return 0;
        return 1;
    }

    //main function - execution begins here
    public static void main(String args[]){
        new KMedoid();
    }

}
