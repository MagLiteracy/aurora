package com.paypal.aurora

class EmailController {

    def emailerService

    /**
     * Hit /email?to=me@somewhere.com&subject=hello+world&body=This+is+a+test to send an email to yourself
     */
    def index = {
        // Each "email" is a simple Map
        def email = [
            to: [ params.to ], // "to" expects a List, NOT a single email address
            subject: params.subject,
            text: params.body // "text" is the email body
        ]
        // sendEmails expects a List
        emailerService.sendEmails([email])
        render("done")
    }

    def disable = {
        emailerService.disable()
        render("System emails disabled")
    }

    def enable = {
        emailerService.enable()
        render("System emails enabled")
    }
}

