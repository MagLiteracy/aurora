import os
from setuptools import setup

f = open(os.path.join(os.path.dirname(__file__), 'README.txt'))
long_description = f.read().strip()
f.close()

setup(
    name='nose-advancedlogging',
    version='0.1',
    author='Anand Palanisamy',
    author_email='apalanisamy@paypal.com',
    description='Advanced logging for nosetests.',
    long_description=long_description,
    license='Apache License 2.0',
    py_modules=['advancedlogging'],
    entry_points={
        'nose.plugins.0.10': [
            'advancedlogging = advancedlogging:AdvancedLogging',
        ]
    },
    install_requires=['beautifulsoup4>=4.2.1'],
    platforms='any',
    zip_safe=False
)
