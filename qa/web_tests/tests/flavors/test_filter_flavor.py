from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from random import randint
import unittest, time
from qa.web_tests import config

class TestFilterFlavor(unittest.TestCase):
    
    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)
    
    def test_filter_flavor(self):
        driver = self.driver
        driver.maximize_window()
        driver.get(self.base_url + "/")
        driver.find_element_by_name("username").send_keys(config.username)
        driver.find_element_by_name("password").send_keys(config.password)
        driver.find_element_by_css_selector("input.loginSubmit").click()
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Compute"))
        Move.perform()
        driver.find_element_by_link_text("Flavors").click()
        driver.find_element_by_link_text("Create New Flavor").click()
        flavor_name = "Test_flavor_%s" % str(randint(100, 10000))
        driver.find_element_by_id("name").send_keys(flavor_name)
        driver.find_element_by_name("_action_save").click()
        driver.find_element_by_xpath("//div/input[@type='text']").send_keys("Test")
        self.assertTrue(self.is_element_present(By.XPATH, "//tbody/tr/td[text()='%s']" % flavor_name))
        _all_elem  = len(driver.find_elements_by_xpath("//tbody/tr"))
        _elem_disp = len(driver.find_elements_by_xpath("//tbody/tr[@style='display: none;']"))
        self.assertTrue(_all_elem - _elem_disp==int(driver.find_elements_by_xpath("//div/label")[1].text))
        driver.find_element_by_xpath("//tbody/tr[td[text()='%s']]/td/input[@type='checkbox']" % flavor_name).click()
        time.sleep(5)
        driver.find_element_by_xpath('//*[@id="delete"]/span/div').click()
        driver.find_element_by_xpath('//*[@id="btn-confirm"]/span').click()
        #alert = driver.switch_to_alert()
        #alert.accept()
        self.assertFalse(self.is_element_present(By.XPATH, "//tbody/tr/td[text()='%s']" % flavor_name ))
        
        
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
