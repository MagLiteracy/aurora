from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
import unittest
from qa.web_tests import config
import time

class TestEditTenantInfo(unittest.TestCase):
    
    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)
    
    def test_edit_tenant_info(self):
        driver = self.driver
        driver.maximize_window()
        driver.get(self.base_url + "/")
        driver.find_element_by_name("username").send_keys(config.username)
        driver.find_element_by_name("password").send_keys(config.password)
        driver.find_element_by_css_selector("input.loginSubmit").click()
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Settings"))
        Move.perform()
        driver.find_element_by_link_text("Tenants").click()
        driver.find_element_by_link_text("Create Tenant").click()
        driver.find_element_by_id("name").send_keys("Test_tenant")
        driver.find_element_by_id("description").send_keys("Test_description")
        driver.find_element_by_id("enabled").click()
        driver.find_element_by_name("_action_save").click()
        driver.find_element_by_xpath("//div/input[@type='text']").send_keys("Test_tenant")
        time.sleep(1)
        self.assertTrue(driver.find_element_by_xpath("//tbody/tr/td[text()='Test_tenant']").is_displayed())
        elements = driver.find_elements_by_xpath("//tbody/tr/td/a")
        time.sleep(1)
        for i in elements:
            if i.is_displayed() != 0:
                i.click()
                break
        time.sleep(5)
        driver.find_element_by_link_text("Edit Tenant").click()
        driver.find_element_by_id("name").clear()
        driver.find_element_by_id("name").send_keys("Test_tenant_edited")
        driver.find_element_by_id("description").clear()
        driver.find_element_by_id("description").send_keys("Test_description_edited")
        driver.find_element_by_name("_action_update").click()
        self.assertTrue(driver.find_element_by_xpath("//tbody/tr/td[text()='Test_tenant_edited']").is_displayed())
        driver.find_element_by_xpath("//div/input[@type='text']").clear()
        driver.find_element_by_xpath("//div/input[@type='text']").send_keys("Test_tenant")
        elements = driver.find_elements_by_xpath("//tbody/tr/td/a")
        time.sleep(1)
        for i in elements:
            if i.is_displayed() != 0:
                i.click()
                break
        #driver.find_element_by_name("_action_delete").click()
        driver.find_element_by_xpath('//*[@id="delete"]/span/div').click()
        driver.find_element_by_xpath('//*[@id="btn-confirm"]/span').click()
        #alert = driver.switch_to_alert()
        #alert.accept()
        self.assertFalse(self.is_element_present(By.XPATH, "//tbody/tr/td[text()='Test_tenant']"))
        
        
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
