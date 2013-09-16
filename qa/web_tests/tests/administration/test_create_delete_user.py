from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support.ui import Select
import unittest
from qa.web_tests import config
import time
import random

class TestCreateDeleteUser(unittest.TestCase):

    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)

    def test_create_delete_user(self):
        user_name = "test_user_ui_%s" % str(random.randint(1,100))
        driver = self.driver
        driver.maximize_window()
        driver.get(self.base_url + "/")
        driver.find_element_by_name("username").send_keys(config.username)
        driver.find_element_by_name("password").send_keys(config.password)
        driver.find_element_by_css_selector("input.loginSubmit").click()
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Settings"))
        Move.perform()
        driver.find_element_by_link_text("Users").click()
        #Creation
        driver.find_element_by_link_text("Create User").click()
        driver.find_element_by_id("name").send_keys(user_name)
        driver.find_element_by_id("email").send_keys("%s@example.com"
                                                     % user_name)
        driver.find_element_by_id("password").send_keys("password")
        driver.find_element_by_id("confirm_password").send_keys("password")
        driver.find_element(By.XPATH, "//input[@id='select_tenant_id']").click()
        driver.find_element_by_link_text("openstack").click()
        driver.find_element(By.XPATH, "//input[@id='select_role_id']").click()
        driver.find_element_by_link_text("Member").click()
        driver.find_element_by_name("_action_save").click()
        time.sleep(3)
        self.assertTrue(driver.find_element_by_xpath(
            "//tbody/tr/td[text()='%s']" % user_name).is_displayed())
        #deletion
        driver.find_element_by_xpath(
            "//*[text()='%s']/../td/input[@type='checkbox']"
            % user_name).click()
        driver.find_element_by_name("_action_delete").click()
        driver.find_element_by_id("btn-confirm").click()
        time.sleep(1)
        self.assertFalse(self.is_element_present(
            By.XPATH,"//tbody/tr/td[text()='%s']" % user_name))

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