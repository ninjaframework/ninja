## Dockerfiles

The Dockerfiles in this directory and the corresponding `build-run-x.sh` scripts
are temporarily here to help test the Ninja servlet integration WAR in various
servlet containers.  We may remove these at some point in the future or try to
integrate them into the maven build process, etc.

To use them, you'll want to build and install the entire Ninja project first.
Then you can simply run `build-run-jetty.sh` to package up a .war file, build
a Docker container, and then run the war in that.