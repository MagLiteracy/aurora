from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from random import randint
import unittest
#from qa.web_tests import config
import time

class TestEditGroupRules(unittest.TestCase):

    def setUp(self):
        self.base_url = "http://172.18.198.44:8080/"
        self.implicitly_wait = 10
        self.username = "admin"
        self.password = "local1lab"
        self.screen_path = "/error.png"
        #flavor_xpath = '//*[@id="select_cb_flavor_item_t1.micro"]'
        #self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(self.implicitly_wait)

    def test_edit_group_rules(self):
        driver = self.driver
        driver.maximize_window()
        driver.get(self.base_url + "/")
        driver.find_element_by_name("username").send_keys(self.username)
        driver.find_element_by_name("password").send_keys(self.password)
        driver.find_element_by_css_selector("input.loginSubmit").click()
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Security"))
        Move.perform()
        driver.find_element_by_link_text("Security Groups").click()
        #creation
        driver.find_element_by_id("create").click()
        security_group = "Test_group_%s" % str(randint(100,10000))
        driver.find_element_by_id("input_sgCreate_name").send_keys(security_group)
        driver.find_element_by_id("input_sgCreate_description").send_keys("Test_description")
        driver.find_element_by_id("submit").click()
        self.assertTrue(self.is_element_present(By.XPATH, "//*[text()='%s']" % security_group))
        #edit group rules
        driver.find_element_by_xpath('//*[text()="%s"]/..//*[@class="securityGroup"]' % security_group).click()
        if self.is_element_present(By.XPATH,"//table[id='table_securityGroupShow']/tbody/tr"):
            count_rules=len(driver.find_elements_by_xpath("//table[id='table_securityGroupShow']/tbody/tr"))
        else:
            count_rules=0
        driver.find_element_by_link_text("Edit rules").click()
        #add new rule
        driver.find_element_by_id("select_select_sgShow_ipProtocol").click()
        driver.find_element_by_link_text("TCP").click()
        driver.find_element_by_id("input_sgShow_fromPort").send_keys("100")
        driver.find_element_by_id("input_sgShow_toPort").send_keys("100")
        driver.find_element_by_id("select_select_sgShow_sourceGroup").click()
        driver.find_element_by_link_text("%s" % security_group).click()
        driver.find_element_by_name("_action_addRule").click()
        time.sleep(1)
        driver.find_element_by_id("upButton").click()
        self.assertEquals(len(driver.find_elements_by_xpath("//table[@id='table_securityGroupShow']/tbody/tr")),count_rules+1)
        #delete added rule
        driver.find_element_by_link_text("Edit rules").click()
        driver.find_element_by_xpath(
            "//table[@id='table_securityGroupEditRules']/tbody/tr['%s']/td/input[@type='checkbox']" % (count_rules)).click()
        driver.find_element_by_name("_action_deleteRule").click()
        driver.find_element_by_id("btn-confirm").click()
        time.sleep(1)
        driver.find_element_by_id("upButton").click()
        if count_rules==0:
            self.assertFalse(self.is_element_present(By.XPATH,"//table[id='table_securityGroupShow']/tbody/tr"))
        else:
            self.assertEquals(len(driver.find_elements_by_xpath("//table[@id='table_securityGroupShow']/tbody/tr")),count_rules)
        #deletion
        driver.find_element_by_id('delete').click()
        driver.find_element_by_id('btn-confirm').click()
        self.assertFalse(self.is_element_present(By.XPATH, "//*[text()='%s']" % security_group))

    def is_element_present(self, how, what):
        try: self.driver.find_element(by=how, value=what)
        except NoSuchElementException, e: return False
        return True

    def tearDown(self):
        self.driver.save_screenshot(self.screen_path)
        self.driver.quit()
        self.assertEqual([], self.verificationErrors)

if __name__ == "__main__":
    unittest.main()