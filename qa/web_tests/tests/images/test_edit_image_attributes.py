from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
import unittest
from qa.web_tests import config

class TestEditImageAttributes(unittest.TestCase):
    
    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)
    
    def test_edit_image_attributes(self):
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
        _name = driver.find_elements_by_xpath("//tbody/tr/td")[1].text
        _public = driver.find_elements_by_xpath("//tbody/tr/td")[4].text
        if _public=="false": _check = "True"
        else: _check = "False"
        driver.find_element_by_xpath("//tbody/tr/td/a").click()
        driver.find_element_by_xpath('//*[@id="edit"]/span').click()
        driver.find_element_by_name("name").clear()
        driver.find_element_by_name("name").send_keys(_name+"test")
        driver.find_element_by_id("shared").click()
        driver.find_element_by_name("_action_update").click()
        self.assertTrue("test" in driver.find_elements_by_xpath("//tbody/tr/td[@class='value']")[1].text)
        self.assertTrue(_check in driver.find_elements_by_xpath("//tbody/tr/td[@class='value']")[3].text)
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Compute"))
        Move.perform()
        driver.find_element_by_link_text("Images").click()
        self.assertTrue("test" in driver.find_elements_by_xpath("//tbody/tr/td")[1].text)
        self.assertFalse(_public in driver.find_elements_by_xpath("//tbody/tr/td")[4].text)
        driver.find_element_by_xpath("//tbody/tr/td/a").click()
        driver.find_element_by_xpath('//*[@id="edit"]/span').click()
        driver.find_element_by_name("name").clear()
        driver.find_element_by_name("name").send_keys(_name)
        driver.find_element_by_id("shared").click()
        driver.find_element_by_name("_action_update").click()
        
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