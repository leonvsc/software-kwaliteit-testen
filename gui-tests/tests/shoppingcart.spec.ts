import {test, expect, Page} from '@playwright/test';
import exp = require("constants");

test('login', async ({ page }) => {
  await page.goto('https://demowebshop.tricentis.com/login/');

  await page.getByLabel("Email:").fill("n.warnaar@student.avans.nl");
  await page.getByLabel("Password").fill("Hdp64z583!!!d");
  await page.locator(".login-button").click();
  await page.getByRole('link', { name: 'Shopping cart', exact: true }).click();

  const emptyShoppingCart : boolean = await page.getByText('Your Shopping Cart is empty!', {exact: true}).isVisible();

  if (!emptyShoppingCart) {
    await emptyCart(page);
  }

  await page.locator("#small-searchterms").fill("smartphone");
  await page.locator('.search-box-button').click();
  await page.locator(".item-box")
      .filter({hasText: "Smartphone"})
      .locator(".product-box-add-to-cart-button")
      .click();

  await page.locator("#small-searchterms").fill("Casual Golf Belt")
  await page.locator('.search-box-button').click();
  await page.locator(".item-box")
      .filter({hasText: "Casual Golf Belt"})
      .locator(".product-box-add-to-cart-button")
      .click();

  await page.getByRole('link', { name: 'Shopping cart', exact: true }).click();
  const test = page.locator("tr")
      .filter({hasText: "Total:"})
      .locator(".order-total").filter({hasText: "101.00"});

  await expect(test).toContainText("101.00")

  await emptyCart(page);

  await expect(page.getByText('Your Shopping Cart is empty!', {exact: true})).toBeVisible();

  await page.getByRole('link', { name: 'Log out', exact: true }).click();
  await expect(page).toHaveURL("https://demowebshop.tricentis.com");

  await page.close();
});

async function emptyCart(page : Page) {
  const cartItems = await page.locator('.remove-from-cart').count();
  for (let i = 0; i < cartItems; i++) {
    await page.click('[name="removefromcart"]')
    await page.locator(".update-cart-button").click();
  }
}
