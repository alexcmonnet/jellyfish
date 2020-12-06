FROM gitpod/workspace-full

# Install Java 8 for compatibility 
RUN sudo apt-get install openjdk-8-jdk -y
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
ENV PATH=/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH