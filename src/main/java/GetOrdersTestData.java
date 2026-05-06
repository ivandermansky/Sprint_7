public class GetOrdersTestData {
    private int courierId;
    private String nearestStation;
    private int number;
    private int page;

    public GetOrdersTestData(int courierId, String nearestStation, int number, int page) {
        this.courierId = courierId;
        this.nearestStation = nearestStation;
        this.number = number;
        this.page = page;
    }

    public int getCourierId() {
        return courierId;
    }

    public String getNearestStation() {
        return nearestStation;
    }

    public int getNumber() {
        return number;
    }

    public int getPage() {
        return page;
    }

    public void setCourierId(int courierId) {
        this.courierId = courierId;
    }

    public void setNearestStation(String nearestStation) {
        this.nearestStation = nearestStation;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
