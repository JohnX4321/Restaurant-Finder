import com.sun.deploy.net.HttpRequest;
import netscape.javascript.JSObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.Scanner;

import org.json.*;

public class Main {

    private static String city;
    private final static String Google_API_KEY="";
    private final static String Zomato_API_KEY="";
    private final static String OLA_X_APP_TOKEN="";
    private final static String OLA_Authorization="";

    public static void main(String args[]) {

        Scanner sc=new Scanner(System.in);

        System.out.println("Restaurant Finder");
        for (int i=0;i<=20;i++)
            System.out.print("*");

        System.out.println();

        System.out.println("Please enter Your current city name");
        city=sc.nextLine();


       try {

           if(Google_API_KEY==""||Zomato_API_KEY==""||OLA_X_APP_TOKEN==""||OLA_Authorization=="") {
               System.out.println("The Api Key has not been entered");
               System.exit(0);

           }

           URL url=new URL("https://maps.googleapis.com/maps/api/geocode/json?address="+city+"&key="+Google_API_KEY);




           JSONObject json = readJsonFromUrl(url.toString());
           System.out.println(json);
           JSONObject j=json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
           double lat=j.getDouble("lat");
           double lng=j.getDouble("lng");
           //sample values
           //double lat=12.9542946;
           //double lng=77.4908526;
           System.out.println(" Your Location Latitude: "+lat);
           System.out.println("Your Location Longitude: "+lng);



           URL zmturl=new URL("https://developers.zomato.com/api/v2.1/search?entity_type=city&start=0&count=10&lat="+lat+"&lon="+lng+"&radius=1000&sort=real_distance");


           URLConnection zmt=(URLConnection)zmturl.openConnection();
           zmt.addRequestProperty("Accept","application/json");
           zmt.addRequestProperty("user-key",Zomato_API_KEY);
           zmt.connect();

           InputStream z=zmt.getInputStream();
           BufferedReader rd=new BufferedReader(new InputStreamReader(z,Charset.forName("UTF-8")));
           String jsonText=readAll(rd);
           JSONObject jso=new JSONObject(jsonText);

          // JSONObject jso=readJsonFromUrl(zmturl.toString());

           Random random=new Random();
           int index=random.nextInt(10);

           JSONObject zomato=jso.getJSONArray("restaurants").getJSONObject(index).getJSONObject("restaurant");
           JSONObject loc=zomato.getJSONObject("location");
           double zlat=loc.getDouble("latitude");
           double zlong=loc.getDouble("longitude");
           String address=loc.getString("address")+","+loc.getString("locality");
           System.out.print("Nearest Restaurant is: \t");
           System.out.println(zomato.getString("name"));
           System.out.println("Restuarant Location: "+address);

           System.out.println("\n \n \n");
           System.out.println("You can travel to your location by OlaCabs");
           System.out.println("Connecting to Ola....");
           System.out.println("\n \n \n");

           URL olaurl=new URL("https://devapi.olacabs.com/v1/products?pickup_lat="+lat+"&pickup_lng="+lng+"&drop_lat="+zlat+"&drop_lng="+zlong+"&service_type=p2p&category=auto");
           URLConnection ola=(URLConnection)olaurl.openConnection();
           ola.addRequestProperty("x-app-token",OLA_X_APP_TOKEN);
           ola.addRequestProperty("Authorization",OLA_Authorization);
           ola.connect();
           float rides[]=null;
           String categories[]=null;

           InputStream o=ola.getInputStream();
           BufferedReader od=new BufferedReader(new InputStreamReader(o,Charset.forName("UTF-8")));
           String olaText=readAll(rd);
           JSONObject olajson=new JSONObject(olaText);
           for (int i=0;i<7;i++) {
                rides[i]= (olajson.getJSONArray("ride_estimate").getJSONObject(i).getInt("amount_min") + olajson.getJSONArray("ride_estimate").getJSONObject(0).getInt("amount_max")) / 2;
                categories[i]=olajson.getJSONArray("ride_estimate").getJSONObject(i).getString("category");
           }


           for (int i=0;i<rides.length;i++)
           {
               //System.out.println("category: "+categories[i]+"\t ride_estimate: "+rides[i]);
               System.out.println("category: "+categories[i]+"\t ride_estimate: "+rides[i]);
           }

           System.out.println("\n \n");
           System.out.println("Thank you for using Restaurant Finder. Hope you visit us again");




       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb=new StringBuilder();
        int cp;
        while ((cp=rd.read())!=-1) {
            sb.append((char)cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException,JSONException {
        InputStream is=new URL(url).openStream();
        try {
            BufferedReader rd=new BufferedReader(new InputStreamReader(is,Charset.forName("UTF-8")));
            String jsonText=readAll(rd);
            JSONObject json=new JSONObject(jsonText);



            return json;
        } finally {
            is.close();
        }
    }



}
