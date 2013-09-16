from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support.ui import Select, WebDriverWait
from bs4 import BeautifulSoup
from re import compile, match, search, IGNORECASE
from time import sleep


class UIDriver(object):

    def __init__(self, config, uimap, utils):
        """
        Arguments:
          - config: dictionary from config file
          - uimap: UIMap instance
          - utils: Utils instance
        """
        self.config = config
        self.uimap = uimap
        self.utils = utils
        self.start_webdriver()

    def start_webdriver(self):
        browser = self.config['selenium']['browser']
        if browser == "ie":
            self.webdriver = webdriver.Ie()
            self.webdriver.maximize_window()
        elif browser == "chrome":
            options = Options()
            options.add_argument("--start-maximized")
            # to make Chrome working it's necessary to download chromedriver, put it, for example, to /opt
            # and set executable_path parameter. symlink to /opt/chromedriver does not work...
            self.webdriver = webdriver.Chrome(chrome_options=options, executable_path='/opt/chromedriver')
        else:
            self.webdriver = webdriver.Firefox()
            self.webdriver.maximize_window()

        self.webdriver.implicitly_wait(self.config["selenium"]['implicit_wait'])
        self.webdriver.get(self.utils.base_url)

    def stop_webdriver(self):
        self.webdriver.quit()

    def click_menu(self, main_menu, sub_menu):
        try:
            el = ActionChains(self.webdriver).move_to_element(
                self.webdriver.find_element(*main_menu))
            el.perform()
            self.click(*sub_menu)
            self.filter_table('')
        except NoSuchElementException, e:
            return False
        return True

    def is_element_present(self, locator_type, locator):
        try:
            self.webdriver.implicitly_wait(0)
            self.webdriver.find_element(locator_type, locator)
            self.webdriver.implicitly_wait(self.config['selenium']['implicit_wait'])
        except NoSuchElementException, e:
            return False
        return True

    def try_click(self, element):
        try:
            element.click()
        except NoSuchElementException, e:
            return False
        return True

    ## TABLES MANIPULATION METHODS ########################################################################
    def parse_details_table(self, tablelocator):
        """
        Parse Details table and return dictionary with parameter name-value pairs.
        Arguments:
          - tablelocator: tuple (usually from UIMap class) containing:
                          - locator type (usually, 'id')
                          - locator value (string)
        """
        table = {}
        rows_xpath = "//*[@%s='%s']/tbody/tr" % tablelocator
        rows = self.find_elements('xpath', rows_xpath)

        for row in rows:
            row_elements = row.find_elements('xpath', 'td')
            # sometimes (rarely) an empty row exists. skip it.
            if len(row_elements) == 0:
                continue
            # parameter name
            key = row_elements[0].text
            # if the row represents section header (see Instance Details) then no key-value is present in it, just key.
            value = None

            if len(row_elements) == 2:  # if key-value is present in the row
                value_el = row_elements[1]
                if value_el.text != '':
                    value = value_el.text
                else:
                    # check if the cell contains nested element containing parameter's value
                    # E.g., Policies Details tbl uses input element to store 'Name' value.
                    try:
                        value = value_el.find_element_by_xpath("./input").get_attribute('value')
                    except NoSuchElementException:
                        value = ''
            table[key] = value
        return table

    def parse_table(self, tablelocator, limit=0):
        """
        Read table specified by (locator_type, locator_value) tuple and put its data to the list of dictionaries.
        Result will look like:
           res_table = [
               {'NAME': 'ui_objectname1', 'STATUS': 'Active', 'SIZE': 100, ...},
               {'NAME': 'ui_objectname2', 'STATUS': 'Build', 'SIZE': 200, ...},
           ]
        where each list element represents one visible row of the table,
        dictionary keys - text of column headers in the table on the page,
        dictionary values - text (in most cases) of the corresponding cell.

        If the 1st column of original table contains checkboxes then checkbox elements are provided in each row-
        dictionary (under key 'x'). So this element can be clicked to be (un)checked.

        If table contains too many rows then it makes sense to apply filter before parsing it to avoid spending
        too much time on parsing useless rows.
        """
        table = []
        headers = [el.text for el in self.get_table_headers(tablelocator)]
        visible_rows_xpath = "//*[@%s='%s']/tbody/tr[not(contains(@style, 'display: none'))]" % tablelocator
        rows = self.find_elements('xpath', visible_rows_xpath)

        n = 0
        for row in rows:
            obj = {}
            row_elements = row.find_elements('xpath', 'td')

            try:
                obj[headers[0]] = row_elements[0].find_element_by_xpath("./input[contains(@id, 'checkBox')]")
            except NoSuchElementException:
                obj[headers[0]] = row_elements[0].text

            for i, column in enumerate(headers[1:]):
                obj[column] = row_elements[i+1].text

            table.append(obj)
            n += 1
            if limit > 0 and n >= limit:
                break
        return table

    def get_tbl_headers(self, tablelocator):
        """
        Get list of web-elements representing table header.
        Can be used to sort table and get names of columns.
        """
        return [el.text for el in self.get_table_headers(tablelocator)]
        # soup = BeautifulSoup(self.find_element(*tablelocator).find_element(*self.uimap.tbl_head).get_attribute("outerHTML"))
        # headers = [el.text for el in soup.find_all("th")]
        # return headers

    def get_table_content(self, tablelocator, limit=0):
        """
        Read table specified by (locator_type, locator_value) tuple and put its data to the dictionary of lists.
        Result will look like:
           res_table = {
            'NAME':
                ['ui_objectname1','ui_objectname2'],
            'STATUS':
                ['Active', 'Build'],
            'SIZE':
                [100, 200],
            '...':
                [...],
            }
        Headers in table always will be UPPER CASE

        where each list element represents one visible row of the table,
        dictionary keys - text of column headers in the table on the page,
        dictionary values - text (in most cases) of the corresponding cell.

        If the 1st column of original table contains checkboxes then checkbox elements are provided in each row-
        dictionary (under key 'x'). So this element can be clicked to be (un)checked.

        If table contains too many rows then it makes sense to apply filter before parsing it to avoid spending
        too much time on parsing useless rows.
        """

        n = 0
        content = {}
        headers = [el.text for el in self.get_table_headers(tablelocator)]
        soup = BeautifulSoup(self.find_element(*tablelocator).find_element(*self.uimap.tbl_body).get_attribute("outerHTML"))
        for hd in headers:
            content[hd] = []
        displ = compile("display: none")
        for bs in soup.find_all("tr"):
            # if not search(displ, bs):
            #     continue
            line = bs.find_all("td")
            a = 1 if headers[0] == 'x' or headers[0] == 'X' else 0
            for cell in line[a:]:
                # if search(displ, cell):
                #     continue
                val = cell.get_text().strip()
                content[headers[line.index(cell)].upper()].append(val)
            n += 1
            if limit > 0 and n >= limit:
                break
        return content

    def get_details(self, tablelocator):
        details = {}
        soup = BeautifulSoup(self.find_element(*tablelocator).find_element(*self.uimap.tbl_body).get_attribute("outerHTML"))
        for bs in soup.find_all("tr"):
            line = bs.find_all("td")
            if len(line) == 0:
                continue
            i = 0
            for cell in line:
                val = cell.get_text().strip()
                if i == 0:
                    k = val.replace(":", "").upper()
                else:
                    details[k] = val
                i += 1
        return details

    def get_column_by_name(self, tablelocator, col_name):
        """
        Return list of elements from column col_name.
        """
        return self.get_table_content(tablelocator)[col_name.upper()]

    def get_row_by_element(self, tablelocator, col_name, val):
        """
        Return list of elements from row where column col_name has value val.
        """
        content = self.get_table_content(tablelocator)
        row = []
        for x in content.keys():
            row.append(content[x][content[col_name.upper()].index(val)])
        return row

    def is_element_in_table(self, tablelocator, col_name, val):
        """
        Return list of elements from row where column col_name has value val.
        """
        # self.refresh_page()
        self.wait_for_page_loaded()
        # self.wait_for_ajax_loaded()
        col = self.get_column_by_name(tablelocator, col_name)
        return True if val in col else False

    def list_table_by_rows(self, tablelocator, limit=0):
        """
        Return table as list of rows.
        """
        table = []
        n = 0
        headers = self.get_tbl_headers(tablelocator)
        soup = BeautifulSoup(self.find_element(*tablelocator).find_element(*self.uimap.tbl_body).get_attribute("outerHTML"))
        for bs in soup.find_all("tr"):
            row = []
            line = bs.find_all("td")
            a = 1 if headers[0] == 'x' or headers[0] == 'X' else 0
            for cell in line[a:]:
                val = cell.get_text().strip()
                row.append(val)
            table.append(row)
            n += 1
            if limit > 0 and n >= limit:
                break
        return table

    def find_row_in_table(self, tablelocator, el_name, col_name):
        headers = self.get_tbl_headers(tablelocator)
        soup = BeautifulSoup(self.find_element(*tablelocator).find_element(*self.uimap.tbl_body).get_attribute("outerHTML"))
        is_found = False
        for bs in soup.find_all("tr"):
            row = []
            i = 0
            lst = bs.find_all("td")
            for cell in lst:
                val = cell.get_text().strip()
                row.append(val)
                if val == el_name:
                    if col_name:
                        is_found = True if col_name == headers[i] else False
                    else:
                        is_found = True
                i += 1
            return row if is_found else ""
        if not is_found:
            return ""

    def count_rows_with_filter(self, tablelocator, fltr):
        """
        Return count of rows which contain the filter.
        """
        self.webdriver.refresh()
        rows = self.list_table_by_rows(tablelocator)
        table = []
        for row in rows:
            for cell in row:
                if search(fltr, cell, IGNORECASE):
                    table.append(row)
                    break
        return len(table)

    def get_value_of_element_in_field(self, tablelocator, col_name, el_name, field):
        """
        Return list of elements from row where column col_name has value field.
        """
        content = self.get_table_content(tablelocator)
        return content[field.upper()][content[col_name.upper()].index(el_name)]

    def wait_for_status(self, tablelocator, col_name, el_name, field, status, timeout=100, step=10):
        counter = 0
        period = 2
        while counter < timeout:
            # self.refresh_page()
            # self.wait_for_page_loaded()
            # self.wait_for_ajax_loaded()
            sleep(period)
            counter += period
            state = (self.get_value_of_element_in_field(tablelocator, col_name, el_name, field)).upper()
            if state == status:
                return True
            elif state == "ERROR":
                return False
            else:
                continue
        return False

    def wait_for_deleted(self, tablelocator, col_name, el_name, timeout=100, step=10):
        """
        Wait for object deletion to be completed and the row satisfying `evaluate_row` condition
        disappears from the specified table.

        Arguments description see in wait_for_row_change docstring.

        Return:
          - True if the row is absent in the table. False otherwise.
        """

        counter = 0
        period = 2
        while counter < timeout:
            # self.refresh_page()
            # self.wait_for_page_loaded()
            self.wait_for_ajax_loaded()
            sleep(period)
            counter += period
            if self.is_element_in_table(tablelocator, col_name, el_name):
                if (self.get_value_of_element_in_field(tablelocator, col_name, el_name, "STATUS")).upper() == "ERROR":
                    return False
                else:
                    continue
            else:
                return True
        return False


    def get_table_headers(self, tablelocator):
        """
        Get list of web-elements representing table header.
        Can be used to sort table and get names of columns.
        """
        return self.find_elements('xpath', "//*[@%s='%s']/thead/tr/th" % tablelocator)

    def click_row_checkbox(self, tablelocator, condition):
        """
        Arguments:
          - tablelocator: tuple of strings (type, value)
          - condition: function returning True/False
        """
        self.get_row_checkbox(tablelocator, condition).click()
        self.wait_for_page_loaded()

    def get_row_checkbox(self, tablelocator, condition):
        """
        Arguments:
          - tablelocator: tuple of strings (type, value);
          - condition: function expecting table row (dictionary) as argument and returning True/False.

        Usage Example::
          checkbox = self.uidriver.get_row_checkbox(self.uimap.tbl_instances, lambda row: row['NAME'] == 'ui_utRf')
        """
        table = self.parse_table(tablelocator)
        rows = [r for r in table if condition(r)]
        if len(rows) != 1:
            raise AssertionError("Found %s elements instead of one checkbox." % len(rows))
        return rows[0]['x']

    def find_row(self, tablelocator, condition):
        """
        Return list of row-dictionaries satisfying the condition.
        """
        self.webdriver.refresh()
        table = self.parse_table(tablelocator)
        return [r for r in table if condition(r)]

    def get_column(self, tablelocator, col_name):
        """
        Return list of elements from column col_name.
        """
        # self.webdriver.refresh()
        table = self.parse_table(tablelocator)
        column = []
        for row in table:
            if row.has_key(col_name):
                column.append(row[col_name])
            else:
                print("There is no column '{0}' in the table".format(col_name))
                break
        return column

    def wait_for_row_change(self, tablelocator, evaluate_row, timeout=100, step=10):
        """
        Wait for object modification to be completed and the row in the specified table satisfies
          `evaluate_row` condition.

        Arguments:
          - tablelocator: tuple (type, value) identifying the table element.
          - evaluate_row: function expecting row-dictionary as argument and returning True/False.
          - timeout: time in seconds to wait for object modification to be completed.
          - step: period between table evaluations.

        Return:
          - Updated row-dictionary or False if timeout expired.

        Examples of functions to evaluate table row:
            evaluate_row = lambda row: row['NAME'] == 'ui_volume' and row['STATUS'] == 'available'
            evaluate_row = lambda row: row['NAME'] == 'ui_instance' and row['STATUS'] in ('Active', 'Error')
        """
        res = self.utils.waitfor(lambda: len(self.find_row(tablelocator, evaluate_row)) == 1, timeout, step)
        if res:
            return self.find_row(tablelocator, evaluate_row)
        else:
            return False

    def wait_for_row_deleted(self, tablelocator, evaluate_row, timeout=100, step=10):
        """
        Wait for object deletion to be completed and the row satisfying `evaluate_row` condition
        disappears from the specified table.

        Arguments description see in wait_for_row_change docstring.

        Return:
          - True if the row is absent in the table. False otherwise.
        """
        return self.utils.waitfor(lambda: len(self.find_row(tablelocator, evaluate_row)) == 0, timeout, step)

    def filter_table(self, filter_text, bottom_table=False):
        """
        Enter string to the specified filter field.
        Return:
            - True if string was entered;
            - False if filter field is not present (it should happen if the 1 or no items exist in the table).
        """
        if bottom_table:
            ed_filter = self.uimap.ed_filter2
        else:
            ed_filter = self.uimap.ed_filter

        if self.is_element_present(*ed_filter):
            self.enter_text(ed_filter, filter_text)
            return True
        return False

    ## COMBOBOX MANIPULATION METHODS ###################################################################################
    def get_selected_cb_option(self, input_locator):
        """
        Just in case: selected option can be received from Select element (if Input solution stops working):
        # select = Select(self.find_element(*element_locator))
        # return select.first_selected_option.get_attribute('text')
        """
        return self.find_element(*input_locator).get_attribute('value')

    def get_cb_options(self, input_locator):
        """
        Return list of strings representing combobox options.
        Input and Select elements are placed next to each other in HTML tree.
        Input is 'displayed' and allows to select option. But Select element allows to get list of all options.
        """
        input_element = self.find_element(*input_locator)
        select_element = Select(input_element.find_element('xpath', 'preceding-sibling::select'))
        return [option.get_attribute('text') for option in select_element.options]

    def select_cb_option(self, input_locator, text):
        """
        Arguments:
          - locator: tuple indicating Input element next to Select (combobox) element.
          - text: text of combobox option to be selected.
        """
        self.click(*input_locator)
        self.click('id', "select_%s" % text)
        return self.get_selected_cb_option(input_locator) == text
    ####################################################################################################################

    def refresh_page(self, timeout=30, step=0.2):
        self.webdriver.refresh()

    def wait_for_page_loaded(self, timeout=30, step=0.2):
        condition = lambda: self.webdriver.execute_script('return document.readyState == "complete";') is True
        return self.utils.waitfor(condition, timeout, step)

    def wait_for_ajax_loaded(self, timeout=30, step=1):
        condition = lambda: self.webdriver.execute_script('return window.jQuery.active == 0;') is True
        return self.utils.waitfor(condition, timeout, step)

    def find_element(self, locator_type, locator):
        if locator_type == 'id':
            return self.webdriver.find_element_by_id(locator)
        elif locator_type == 'name':
            return self.webdriver.find_element_by_name(locator)
        elif locator_type == 'xpath':
            return self.webdriver.find_element_by_xpath(locator)
        elif locator_type == 'css' or locator_type == 'css selector':
            return self.webdriver.find_element_by_css_selector(locator)
        elif locator_type == 'link' or locator_type == 'link text':
            return self.webdriver.find_element_by_link_text(locator)
        elif locator_type == 'part_link' or locator_type == 'partial link text':
            return self.webdriver.find_element_by_partial_link_text(locator)
        elif locator_type == 'tag_name' or locator_type == 'tag name':
            return self.webdriver.find_element_by_tag_name(locator)
        elif locator_type == 'class_name' or locator_type == 'class name':
            return self.webdriver.find_element_by_class_name(locator)

    def find_elements(self, locator_type, locator):
        if locator_type == 'id':
            return self.webdriver.find_elements_by_id(locator)
        elif locator_type == 'name':
            return self.webdriver.find_elements_by_name(locator)
        elif locator_type == 'xpath':
            return self.webdriver.find_elements_by_xpath(locator)
        elif locator_type == 'css' or locator_type == 'css selector':
            return self.webdriver.find_elements_by_css_selector(locator)
        elif locator_type == 'link' or locator_type == 'link text':
            return self.webdriver.find_elements_by_link_text(locator)
        elif locator_type == 'part_link' or locator_type == 'partial link text':
            return self.webdriver.find_elements_by_partial_link_text(locator)
        elif locator_type == 'tag_name' or locator_type == 'tag name':
            return self.webdriver.find_elements_by_tag_name(locator)
        elif locator_type == 'class_name' or locator_type == 'class name':
            return self.webdriver.find_elements_by_class_name(locator)

    def enter_text(self, field_locator, text=''):
        """
        Arguments:
          - field_locator: tuple of (locator_type, locator_value);
          - text: string to be sent to the element. By default, element text is just cleared.
        """
        element = self.find_element(*field_locator)
        element.clear()
        if text != '':
            element.send_keys(text)
        self.wait_for_ajax_loaded()

    def click(self, locator_type, locator):
        try:
            element = self.find_element(locator_type, locator)
            element.click()
            self.wait_for_page_loaded()
        except NoSuchElementException, e:
            return False

    def click_element(self, element):
        try:
            element.click()
            self.wait_for_ajax_loaded()
        except NoSuchElementException, e:
            return False

    def wait_for_element_present(self, locator_type, locator, time=10, parent_element=None):
        parent = self if parent_element is None else parent_element
        f = lambda driver: parent.find_element(locator_type, locator)
        return WebDriverWait(self.webdriver, time).until(f, 'Element %s not found after %s seconds.' % (locator, time))

    def chk_error_message(self):
        if self.is_element_present(*self.uimap.msg_error):
            return (False, self.find_element(*self.uimap.msg_error).text)
        else:
            return (True, "")
