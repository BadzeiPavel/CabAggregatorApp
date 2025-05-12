package utils;

import enums.CarCategory;

import java.math.BigDecimal;

public class CalculationUtil {

    private static final float ECONOMY_CAR_CATEGORY_RATE_BY_KM = 0.5F;
    private static final float COMFORT_CAR_CATEGORY_RATE_BY_KM = 0.45F;
    private static final float BUSINESS_CAR_CATEGORY_RATE_BY_KM = 0.35F;

    public static BigDecimal calculateRideCostByDistanceAndCarCategoryAndPromoCode(
            double distance,
            CarCategory carCategory,
            String promoCode
    ) {
        double cost = distance * switch(carCategory) {
            case ECONOMY -> ECONOMY_CAR_CATEGORY_RATE_BY_KM;
            case COMFORT -> COMFORT_CAR_CATEGORY_RATE_BY_KM;
            case BUSINESS -> BUSINESS_CAR_CATEGORY_RATE_BY_KM;
        };

        BigDecimal bigDecimalCost = BigDecimal.valueOf(cost);
        BigDecimal discount = BigDecimal.valueOf((1 - CalculationUtil.promoCodeDiscount(promoCode)));

        return bigDecimalCost.multiply(discount);
    }

    private static float promoCodeDiscount(String inputString) {
        int hashCode = inputString.hashCode();
        double normalizedValue = (double) (hashCode & 0x7FFFFFFF) / Integer.MAX_VALUE;
        double scaledValue = normalizedValue * 0.5;

        return (float) Math.pow(scaledValue, 2);
    }
}
