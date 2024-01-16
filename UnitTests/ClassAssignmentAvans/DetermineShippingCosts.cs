namespace ClassAssignmentAvans;
public class DetermineShippingCosts
{
    public double ShippingCosts(bool calculateShippingCosts, string
        typeOfShippingCosts, double totalPrice)
    {
        double result = 0;
        if (calculateShippingCosts == false)
        {
            result = 0;
            return result;
        }

        if (totalPrice > 1500)
        {
            result = 0;
            return result;
        }

        result = typeOfShippingCosts switch
        {
            "Ground" => 100,
            "InStore" => 50,
            "NextDayAir" => 250,
            "SecondDayAir" => 125,
            _ => 0
        };
        return result;
    }
}

