from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
import unittest
from qa.web_tests import config

class TestFilterImages(unittest.TestCase):
    
    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)
    
    def test_filter_images(self):
        _present_condition = "test"
        _not_present_condition = "F16-1"
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
        driver.find_element_by_xpath("//div/input[@type='text']").send_keys(_present_condition)
        self.assertTrue(driver.find_element_by_xpath('//*[@id="snapshots"]/tbody/tr[1]/td[2]').is_displayed())
        self.assertFalse(self.is_element_present(By.XPATH, "//table[@id='images']/tbody/tr/td[text()=_not_present_condition]"))
        _all_elem  = len(driver.find_elements_by_xpath("//table[@id='images']/tbody/tr"))
        _elem_disp = len(driver.find_elements_by_xpath("//table[@id='images']/tbody/tr[@style='display: none;']"))
        #self.assertTrue(_all_elem - _elem_disp==int(driver.find_elements_by_xpath("//div/label")[1].text))
        
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