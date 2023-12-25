namespace ClassAssignmentAvans.Tests;
using Xunit;


public class DetermineShippingCostsUnitTests
{
    [Fact]
    public void ShippingCosts_IsFalse_Return0()
    {
        // Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        // Act
        var result = shippingCosts.ShippingCosts(false, null, 0.00);
        
        //Assert
        Assert.Equal(0, result);
    }

    [Fact]
    public void ShippingCosts_Above1500_Return0()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, null, 1550);
        
        //Assert
        Assert.Equal(0, result);
    }
    
    [Fact]
    public void ShippingCosts_TypeGroundBelow1500_Return100()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, "Ground", 50);
        
        //Assert
        Assert.Equal(100, result);
    }
    
    [Fact]
    public void ShippingCosts_TypeGroundAbove1500_Return0()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, "Ground", 1550);
        
        //Assert
        Assert.Equal(0, result);
    }
    
    [Fact]
    public void ShippingCosts_TypeInStoreBelow1500_Return50()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, "InStore", 50);
        
        //Assert
        Assert.Equal(50, result);
    }
    
    [Fact]
    public void ShippingCosts_TypeInStoreAbove1500_Return50()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, "InStore", 1550);
        
        //Assert
        Assert.Equal(0, result);
    }
    
    [Fact]
    public void ShippingCosts_TypeNextDayAirBelow1500_Return250()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, "NextDayAir", 50);
        
        //Assert
        Assert.Equal(250, result);
    }
    
    [Fact]
    public void ShippingCosts_TypeNextDayAirAbove1500_Return50()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, "NextDayAir", 1550);
        
        //Assert
        Assert.Equal(0, result);
    }
    
    [Fact]
    public void ShippingCosts_TypeSecondDayAirBelow1500_Return250()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, "SecondDayAir", 50);
        
        //Assert
        Assert.Equal(125, result);
    }
    
    [Fact]
    public void ShippingCosts_TypeSecondDayAirAbove1500_Return50()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, "SecondDayAir", 1550);
        
        //Assert
        Assert.Equal(0, result);
    }
    
    [Fact]
    public void ShippingCosts_TypeNotDefinedBelow1500_Return0()
    {
        //Arrange
        var shippingCosts = new DetermineShippingCosts();
        
        //Act
        var result = shippingCosts.ShippingCosts(true, "ThirdDayAir", 50);
        
        //Assert
        Assert.Equal(0, result);
    }
}