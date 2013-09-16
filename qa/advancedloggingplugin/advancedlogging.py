import os
from os.path import join
import traceback

from bs4 import BeautifulSoup
from nose.plugins import Plugin


class AdvancedLogging(Plugin):

    name = "advanced-logging"
    enabled = False
    capture_screen = True
    score = 1
    _log_path = join(os.getcwd(), 'test_output')
    _script_path = None

    def __init__(self):
        super(AdvancedLogging, self).__init__()
        html_template = """
        <html>
            <head>
                <title></title>
                <style type="text/css">
                    .header {
                        font-weight: bold;
                    }
                    span.fail {
                        color: red;
                    }
                    span.error {
                        color: orange;
                    }
                    span.pass {
                        color: green;
                    }
                </style>
            </head>
            <body><body>
        </html>
        """
        self.soup = BeautifulSoup(html_template)
        self.html = self.soup.body

        title = self.soup.title
        title.string = 'Advanced log'

        self.fieldset = None

    def options(self, parser, env=os.environ):
        parser.add_option(
            "--advanced-logging", action="store_true",
            dest="advancedlogging",
            default=False,
            help="Optional: This will enable advanced logging.")

        parser.add_option(
            "--disable-capture-screen", action="store_false",
            dest="disablecapturescreen",
            default=True,
            help="Optional: This will disable capture screen on failure.")

        parser.add_option(
            "--advanced-log-filename", action="store",
            default='AdvancedLog.html',
            dest="advancedlogfilename",
            help="Optional: Advanced log filename, e.g. Result.html"
                 "default is AdvancedLog.html")

    def configure(self, options, conf):
        if not options.advancedlogging:
            return

        self.enabled = True
        self.capture_screen = options.disablecapturescreen
        self.html_filename = options.advancedlogfilename
        super(AdvancedLogging, self).configure(options, conf)

    def addFailure(self, test, err):
        err = self.formatErr(err)

        span = self.soup.new_tag('span')
        span.string = 'FAIL'
        span['class'] = 'header fail'
        self.testdiv.append(span)
        hr = self.soup.new_tag('hr')
        self.testdiv.append(hr)

        try:
            if self.capture_screen:
                filename = '%s.png' % test.address()[2]
                full_filename = join(self._log_path, filename)
                driver = test.context.uidriver.webdriver
                driver.get_screenshot_as_file(full_filename)
            print 'Screenshot was captured %s' % full_filename
            a = self.soup.new_tag('a')
            a['href'] = filename
            a['target'] = '_blank'
            img = self.soup.new_tag('img')
            img['src'] = filename
            img['alt'] = filename
            img['title'] = filename
            img['width'] = '800px'
            img['border'] = '1'
            a.append(img)
            self.testdiv.append(a)
        except:
            pass

        pre = self.soup.new_tag('pre')
        pre.string = err

        self.testdiv.append(pre)

    def addSuccess(self, test):
        span = self.soup.new_tag('span')
        span.string = 'OK'
        span['class'] = 'header pass'
        self.testdiv.append(span)
        hr = self.soup.new_tag('hr')
        self.testdiv.append(hr)

    def addError(self, test, err):
        try:
            err = self.formatErr(err)
            span = self.soup.new_tag('span')
            span.string = 'ERROR'
            span['class'] = 'header error'
            self.testdiv.append(span)
            hr = self.soup.new_tag('hr')
            self.testdiv.append(hr)
            pre = self.soup.new_tag('pre')
            pre.string = err
            self.testdiv.append(pre)
        except:
            pass

    def finalize(self, result):
        br = self.soup.new_tag('br')
        self.html.append(br)
        div1 = self.soup.new_tag('div')
        div2 = self.soup.new_tag('div')
        self.html.append(div1)
        div1.string = "Ran %d test%s" % \
                      (result.testsRun, result.testsRun != 1 and 's' or '')
        self.html.append(div2)
        span = self.soup.new_tag('span')
        div2.append(span)
        if not result.wasSuccessful():
            span2 = self.soup.new_tag('span')
            span.string = 'FAILED'
            span['class'] = 'header fail'
            span2.string = '(failures=%d errors=%d)' %\
                           (len(result.failures), len(result.errors))
            div2.append(span2)

        else:
            span.string = 'OK'
            span['class'] = 'header pass'

        full_html_filename = join(self._log_path, self.html_filename)
        with open(full_html_filename, 'w') as html_file:
            str_html = self.soup.prettify()
            html_file.write(str_html)

    def formatErr(self, err):
        exctype, value, tb = err
        return ''.join(traceback.format_exception(exctype, value, tb))

    def startContext(self, ctx):
        if hasattr(ctx, '__file__'):
            self._script_path = ctx.__file__.replace('.pyc', '.py')
            return

        try:
            n = ctx.__name__
        except AttributeError:
            n = str(ctx).replace('<', '').replace('>', '')

        self.fieldset = self.soup.new_tag('fieldset')
        legend = self.soup.new_tag('legend')
        span1 = self.soup.new_tag('span')
        span1.string = n
        span1['class'] = 'header'
        legend.append(span1)

        if self._script_path:
            span2 = self.soup.new_tag('span')
            span2.string = '(%s)' % self._script_path
            legend.append(span2)

        self.fieldset.append(legend)
        self.html.append(self.fieldset)

    def stopContext(self, ctx):
        self.fieldset = None

    def startTest(self, test):
        self.testdiv = self.soup.new_tag('div')
        hr = self.soup.new_tag('hr')
        self.testdiv.append(hr)

        span = self.soup.new_tag('span')
        span.string = test.shortDescription() or str(test)
        span['class'] = 'header'
        self.testdiv.append(span)

        self.fieldset.append(self.testdiv)
