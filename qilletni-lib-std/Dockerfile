# Use an official Python runtime as a parent image
FROM python:3.9-slim

# Install Doxygen and other necessary packages
RUN apt-get update && apt-get install -y \
    doxygen \
    graphviz \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the current directory contents into the container at /usr/src/app
COPY . /usr/src/app

# Make the custom_filter.py script executable
RUN chmod +x /usr/src/app/custom_filter.py || true

# Debug: Preprocess files and list files in the working directory
CMD ["sh", "-c", "for file in /usr/src/app/qilletni-src/*.ql; do python3 /usr/src/app/custom_filter.py $file > /usr/src/app/processed/$(basename $file); done && ls -l /usr/src/app/processed && cat /usr/src/app/processed/* && doxygen /usr/src/app/Doxyfile"]
