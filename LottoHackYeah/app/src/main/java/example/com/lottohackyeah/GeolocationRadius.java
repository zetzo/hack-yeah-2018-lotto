package example.com.lottohackyeah;

public class GeolocationRadius {
    public double Latitude;
    public double Longitude;
    public double CircleRadius;

    public GeolocationRadius(){

    }

    public GeolocationRadius(double Latitude, double Longitude, double CircleRadius){
        this.Latitude=Latitude;
        this.Longitude=Longitude;
        this.CircleRadius=CircleRadius;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getCircleRadius() {
        return CircleRadius;
    }

    public void setCircleRadius(double circleRadius) {
        CircleRadius = circleRadius;
    }
}
