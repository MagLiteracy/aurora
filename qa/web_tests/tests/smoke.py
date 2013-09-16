from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
import unittest
from qa.web_tests import config

class UITests(unittest.TestCase):
    
    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)
        
    def test_smoke(self):
        driver = self.driver
        driver.maximize_window()
        driver.get(self.base_url + "/")
        driver.find_element_by_name("username").send_keys(config.username)
        driver.find_element_by_name("password").send_keys(config.password)
        driver.find_element_by_css_selector("input.loginSubmit").click()
        self.assertTrue(self.is_element_present(By.ID, "showDialog"))
        self.assertTrue(self.is_element_present(By.NAME, "_action_showLB"))
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Compute"))
        Move.perform()
        driver.find_element_by_link_text("Images").click()
        self.assertTrue("Images" in driver.find_element_by_xpath("//div/h1").text)
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Image[\s\S]*$")
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Compute"))
        Move.perform()
        driver.find_element_by_link_text("Flavors").click()
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Flavors[\s\S]*$")
        self.assertTrue(self.is_element_present(By.LINK_TEXT, "Create New Flavor"))
        self.assertTrue(self.is_element_present(By.NAME, "_action_delete"))
        self.assertTrue(self.is_element_present(By.ID, "selectedFlavors"))
        driver.find_element_by_link_text("Security").click()
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Security Groups[\s\S]*$")
        self.assertTrue(self.is_element_present(By.LINK_TEXT, "Create New Security Group"))
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Security"))
        Move.perform()
        driver.find_element_by_link_text("Keypairs").click()
        self.assertTrue(self.is_element_present(By.LINK_TEXT, "Create New Keypair"))
        self.assertTrue(self.is_element_present(By.XPATH, "//div[@id='main']/div/form/div/div/a[2]/span"))
        self.assertTrue(self.is_element_present(By.NAME, "_action_delete"))
        self.assertTrue(self.is_element_present(By.ID, "selectedKeypairs"))
        driver.find_element_by_link_text("Storage").click()
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Volumes[\s\S]*$")
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Storage"))
        Move.perform()
        driver.find_element_by_link_text("Snapshots").click()
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Volume Snapshots[\s\S]*$")
        driver.find_element_by_link_text("Settings").click()
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Quotas[\s\S]*$")
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Cores[\s\S]*$")
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Settings"))
        Move.perform()
        driver.find_element_by_link_text("Services").click()
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Services[\s\S]*$")
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*nova[\s\S]*$")
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Settings"))
        Move.perform()
        driver.find_element_by_link_text("Tenants").click()
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Tenants[\s\S]*$")
        self.assertTrue(self.is_element_present(By.LINK_TEXT, "Create Tenant"))
        self.assertTrue(self.is_element_present(By.NAME, "_action_delete"))
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*admin[\s\S]*$")
        driver.find_element_by_link_text("Heat").click()
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Stacks[\s\S]*$")
        self.assertTrue(self.is_element_present(By.ID, "showUploadFileDialog"))
        self.assertTrue(self.is_element_present(By.NAME, "_action_delete"))
        driver.find_element_by_link_text("LBaaS").click()
        self.assertRegexpMatches(driver.find_element_by_css_selector("BODY").text, r"^[\s\S]*Pools & Services[\s\S]*$")
        self.assertTrue(self.is_element_present(By.ID, "showDialog"))
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("LBaaS"))
        Move.perform()
        driver.find_element_by_link_text("Jobs").click()
        self.assertTrue(self.is_element_present(By.XPATH, "//thead/tr/th[text()='ID']"))
        Move = ActionChains(driver).move_to_element(driver.find_element_by_xpath("//div[@class='authentication']"))
        Move.perform()
        driver.find_element_by_link_text("Logout").click()
        
    def is_element_present(self, how, what):
        try: self.driver.find_element(by=how, value=what)
        except NoSuchElementException, e: return False
        return True
    
    def tearDown(self):
        self.driver.save_screenshot(config.screen_path)
        self.driver.quit()
        self.assertEqual([], self.verificationErrors)

if __name__ == "__main__":
    unittest.main()
