# Shopping Chart Application written in JAVA 11

```java
 void addItem(Product p, int amount);
```
adds new item to the chart

```java
void removeItem(Product p, int amount);
```

removes an item from the chart

```java
void applyDiscounts(Campaign... campaigns);
```

applies campaign discounts. if multiple, only the one with the maximum discount is applied

```java
void applyCoupon(Coupon coupon);
```
applies coupon discount. first a campaign must be applied. if the chart limit lower than coupon limit, coupon is not applied.

```java
double getCouponDiscounts();
```
returns discounts from coupons

```java
double getCampaignDiscount();
```
returns discounts from campaigns

```java
double getTotalAmountAfterDiscounts();
```
returns total chart price after applying campaign and coupon discounts

```java
int getNumberOfProducts();
```
returns number of distinct products.

```java
int getNumberOfDistinctCategories();
```
returns number of distinct categories

```java
double getDeliveryCost();
```
calculates and returns delivery cost for the chart
delivery cost is calculated as 
```
CostPerDelivery*NumberOfDeliveries + CostPerProduct*NumberOfProducts + FixedCost
```

Fixed cost is `2.99 TL`, Cost per delivery is `20.00 TL`, CostPerProduct is `6.00 TL`

```java
String print();
```
prints the chart content along with the total cost and total delivery cost
