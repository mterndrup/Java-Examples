package Beans;

public class EnergySession {
   private String date;
   private float totalKwH;
   private float totalDollars;

   public String getDate() {
      return this.date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public float getTotalKwH() {
      return this.totalKwH;
   }

   public void setTotalKwH(float totalKwH) {
      this.totalKwH = totalKwH;
   }

   public float getTotalDollars() {
      return this.totalDollars;
   }

   public void setTotalDollars(float totalDollars) {
      this.totalDollars = totalDollars;
   }
}
