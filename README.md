arduino-serial
==============

This API, along with its accompanying Arduino sketch, defines a relatively light
protocol for sending commands to an Arduino over a USB serial connection.  The
defining feature is the ability to assign initial values for each command and
have the commands revert to those initial values if the API stops sending data
to the Arduino for some configurable period of time.

The primary idea (that was the genesis of this API) is to be able to define
simple commands for controlling speeds of DC motors and positions of servos.
If the serial connection or API host goes down, the Arduino sketch should
quickly notice this and set motor speeds and servo positions to their initial
values (e.g. **zero** or **stop** in the case of DC motors; some neutral or
home position for servos).

Serial strings sent from the API to the Arduino will look something like this:

    I,SERVO_01:90                                         - initialize SERVO_01 command to 90
    I,MOTOR_01:0                                          - initialize MOTOR_01 command to 0
    C                                                     - a lone update character
    C,MOTOR_01:100                                        - update MOTOR_01 command to 100
    C,MOTOR_01:100,MOTOR_02:100,SERVO_01:0,SERVO_02:180   - update several commands

Note that each string would be terminated with a newline ("\n") character.
See the API documentation or the Arduino sketch for more insight into the
structure of these strings.

The API sends updated command values to the Arduino at regular intervals rather
then at the very moment that they're changed.  The update interval is configurable
from five to twenty updates per second.  If a command value changes, the next
update string will contain the name and updated value of that command.

If no command values have changed since the last update, then a lone command
character ("C") terminated by a newline character will be sent.  This lets
the Arduino sketch know that the serial connection is still active.

If the Arduino sketch doesn't receive a certain number of updates, it'll assume
that the serial connection or API host is no longer operative and set all
commands to their initial values.  The number of missed updates is configurable
from three to one hundred.

## Usage

To use this API, you'll need to update your build script to retrieve the binaries from
[Bintray](https://bintray.com/steasdal/arduino/arduino-serial) and add them to your
project's classpath.

### As a Grails dependency

    repositories {
        mavenRepo "http://dl.bintray.com/steasdal/arduino"
    }

    dependencies {
        runtime "org.teasdale:arduino-serial:0.3"
    }

### As a Gradle dependency

    repositories {
        maven {
            url "http://dl.bintray.com/steasdal/arduino"
        }
    }

    dependencies {
        runtime "org.teasdale:arduino-serial:0.3"
    }
