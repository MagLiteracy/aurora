from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from random import randint
import unittest
from qa.web_tests import config
import time

class TestAddDeleteService(unittest.TestCase):
    
    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)
    
    def test_add_delete_service(self):
        driver = self.driver
        driver.maximize_window()
        driver.get(self.base_url + "/")
        driver.find_element_by_name("username").send_keys(config.username)
        driver.find_element_by_name("password").send_keys(config.password)
        driver.find_element_by_css_selector("input.loginSubmit").click()
        Move = ActionChains(driver).move_to_element(driver.find_element_by_xpath('//*[@id="nav-lbaas-listPools-root"]'))
        Move.perform()
        driver.find_element_by_xpath('//*[@id="nav-lbaas-listPools"]').click()
        driver.find_element_by_xpath('//*[@id="addPool"]').click()
        pool_name = 'Test_pool_am_%s' % str(randint(100, 10000))
        driver.find_element_by_id("name").send_keys(pool_name)
        driver.find_element_by_id("port").send_keys("1234")
        driver.find_element_by_id("monitors-health").click()
        driver.find_element_by_id('submit').click()
        self.assertTrue(driver.find_element_by_xpath("//*[text()='%s']" % pool_name).is_displayed())
        driver.find_element_by_xpath("//*[text()='%s']" % pool_name).click()
        driver.find_element_by_id('addService').click()
        driver.find_element_by_id("name").send_keys("test_service")
        driver.find_element_by_id("port").send_keys("10")
        driver.find_element_by_id("weight").send_keys("10")
        driver.find_element_by_id("submit").click()
        self.assertTrue(driver.find_element_by_xpath("//tbody/tr/td[text()='test_service']").is_displayed())
        driver.find_element_by_xpath("//tbody/tr/td/input[@value='test_service']").click()
        driver.find_element_by_id('delete').click()
        driver.find_element_by_id('btn-confirm').click()
        self.assertFalse(self.is_element_present(By.XPATH, "//tbody/tr/td[text()='test_service']"))
        Move = ActionChains(driver).move_to_element(driver.find_element_by_xpath('//*[@id="nav-lbaas-listPools-root"]'))
        Move.perform()
        driver.find_element_by_xpath('//*[@id="nav-lbaas-listPools"]').click()
        driver.find_element_by_xpath("//tbody/tr/td/input[@value='%s']" % pool_name).click()
        driver.find_element_by_id("delete").click()
        driver.find_element_by_id('btn-confirm').click()
        self.assertFalse(self.is_element_present(By.XPATH, "//*[text()='%s']" % pool_name))
        
        
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
