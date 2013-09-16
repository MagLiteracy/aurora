from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from random import randint
import unittest
from qa.web_tests import config

class TestShowGroupDetails(unittest.TestCase):
    
    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)
    
    def test_show_group_details(self):
        driver = self.driver
        driver.maximize_window()
        driver.get(self.base_url + "/")
        driver.find_element_by_name("username").send_keys(config.username)
        driver.find_element_by_name("password").send_keys(config.password)
        driver.find_element_by_css_selector("input.loginSubmit").click()
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Security"))
        Move.perform()
        driver.find_element_by_link_text("Security Groups").click()
        driver.find_element_by_id("create").click()
        security_group = "Test_group_%s" % str(randint(100,10000))
        driver.find_element_by_id("input_sgCreate_name").send_keys(security_group)
        driver.find_element_by_id("input_sgCreate_description").send_keys("Test_description")
        driver.find_element_by_id("submit").click()
        self.assertTrue(self.is_element_present(By.XPATH, "//*[text()='%s']" % security_group))
        driver.find_element_by_xpath('//*[text()="%s"]/..//*[@class="securityGroup"]' % security_group).click()
        self.assertTrue(self.is_element_present(By.XPATH, '//*[text()="%s"]' % security_group))
        self.assertTrue(self.is_element_present(By.XPATH, '//*[text()="Test_description"]'))
        driver.find_element_by_id('delete').click()
        driver.find_element_by_id('btn-confirm').click()
        self.assertFalse(self.is_element_present(By.XPATH, "//*[text()='%s']" % security_group))
        
        
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