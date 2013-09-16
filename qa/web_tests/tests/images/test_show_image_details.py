from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
import unittest
from qa.web_tests import config

class TestShowImageDetails(unittest.TestCase):
    
    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)
    
    def test_show_image_details(self):
        driver = self.driver
        driver.maximize_window()
        driver.set_script_timeout(5)
        driver.set_page_load_timeout(5)
        driver.get(self.base_url + "/")
        driver.find_element_by_name("username").send_keys(config.username)
        driver.find_element_by_name("password").send_keys(config.password)
        driver.find_element_by_css_selector("input.loginSubmit").click()
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Compute"))
        Move.perform()
        driver.find_element_by_link_text("Images").click()
        #driver.find_element_by_xpath("//tbody/tr/td/a").click()
        #driver.find_element_by_xpath('//*[@title="Show details of this Image"]').click()
        Move = ActionChains(driver).move_to_element(driver.find_element_by_xpath('//*[@class="image"]'))
        Move.perform()
        driver.find_element_by_xpath('//*[@class="image"]').click()
        #self.assertTrue("Image Details" in driver.find_element_by_xpath("//div/div/h1").text)
        self.assertTrue(self.is_element_present(By.XPATH, '//*[@id="upButton"]'))
        self.assertTrue(self.is_element_present(By.XPATH, '//*[@id="edit"]/span'))
        self.assertTrue(self.is_element_present(By.XPATH, '//*[@id="delete"]/span/div'))
        
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