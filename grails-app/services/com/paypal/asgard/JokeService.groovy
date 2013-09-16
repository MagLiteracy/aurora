package com.paypal.asgard

import com.google.common.collect.Lists
import com.paypal.asgard.joke.FailureImage
import com.paypal.asgard.joke.ImageAttributions

class JokeService {

    def configService

    private static final Random RANDOM = new Random()

    private final List<String> exclamations = ['Oh snap', 'Oops', 'Whoops', 'Sorry', 'My bad', 'Uh oh', 'Shoot',
            'Bummer', 'Blast', 'Oh no', 'Holy guacamole', 'Frak', "D'oh", 'Woah'].asImmutable()

    private final List<String> excuses = ["That totally didn't work right", 'An evil monkey cursed your request',
            "Don't you wish computers just worked?", 'Something went hideously wrong',
            'There was a failure in the flux capacitor', 'There was a failure in the sonic transducer',
            'Is this what they meant by "adequate performance"?',
            'The developer will get a generous severance package',
            'Something sank your battleship', 'Blame the dog', 'Blame the cat', 'The code broke',
            "That wasn't supposed to happen", 'Not enough test coverage', "Let's blame Microsoft",
            "Let's blame Oracle", 'I sense a disruption in the force', "Don't blame yourself",
            'An interaction between man and machine has failed today'].asImmutable()

    private final List<FailureImage> failureImages = ImageAttributions.FAILURE_IMAGES

    private static FilenameFilter IMAGE_FILE_NAME_FILTER = { File containerDir, String fileName ->
        fileName.endsWith('.jpg') || fileName.endsWith('.gif') || fileName.endsWith('.png')
    } as FilenameFilter

    String randomExclamation() {
        randomItem(exclamations)
    }

    String randomExcuse() {
        randomItem(excuses)
    }

    FailureImage randomFailureImage() {
        List<FailureImage> failureImages = Lists.newArrayList(failureImages)
        try {
            failureImages += new File("${configService.asgardHome}/images/failure").listFiles(IMAGE_FILE_NAME_FILTER).
                    collect { new FailureImage(path: "/externalImage/failure/${it.name}")  }
        } catch (Exception e) {
            log.error('Error loading failure images', e)
        }
        randomItem(failureImages)
    }

    private <T> T randomItem(List<T> items) {
        items[RANDOM.nextInt(items.size())]
    }
}
