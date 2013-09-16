from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from random import randint
import unittest
from qa.web_tests import config

class TestImportKeypairs(unittest.TestCase):
    
    def setUp(self):
        self.base_url = config.base_url
        self.verificationErrors = []
        self.accept_next_alert = True
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(config.implicitly_wait)
    
    def test_import_keypairs(self):
        driver = self.driver
        driver.maximize_window()
        driver.get(self.base_url + "/")
        driver.find_element_by_name("username").send_keys(config.username)
        driver.find_element_by_name("password").send_keys(config.password)
        driver.find_element_by_css_selector("input.loginSubmit").click()
        Move = ActionChains(driver).move_to_element(driver.find_element_by_link_text("Security"))
        Move.perform()
        driver.find_element_by_link_text("Keypairs").click()
        driver.find_element_by_link_text("Import Keypair").click()
        keypair_name = "Test_keypair_%s" % str(randint(100, 10000))
        driver.find_element_by_name("name").send_keys(keypair_name)
        driver.find_element_by_name("publicKey").send_keys("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDhYgV5M4euYh3fJNojpzmQ/WA0LGuPiGH2OuTn2pYdl8LV7AveBnwN8oiO5KkRZdcsS1NIMgGBrpMsxtOOHVipnZQ1jtK5TRx6IY9o09pq2kNMHXS2852+sSHNLFaq7f7O4/CUrMzhdH/IM9FZ6XjIpIyAgIbdmaSgYYGvnE0EY7zlBbURxifvDHXXntbVV4oIBiYBfw5M0ubazM29/XTcZBMEhUgvTA7asLaMnTGdUdbn1BGMkLBeCmt2GyFPIGOH3FCHt1BeXu/a7BEr2962e9WB2U+EAcgQUdDcKDXAtvfiQg7v9bJpHK9ZZVmSFOHKWZ7RXc5xB8niRtwOyvR3 qa@ubuntu")
        driver.find_element_by_id("submit").click()

        self.assertTrue(self.is_element_present(By.XPATH, '//*[@value="%s"]'
                                                          % keypair_name))
        driver.find_element_by_xpath('//*[@value="%s"]' % keypair_name).click()
        driver.find_element_by_xpath('//*[@id="delete"]/span/div').click()
        driver.find_element_by_xpath('//*[@id="btn-confirm"]/span').click()
        self.assertFalse(self.is_element_present(By.XPATH, '//*[@value="%s"]'
                                                           % keypair_name))

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
