import os
import yaml

def load_config():
    # Get the directory where this script is located
    lib_directory = os.path.dirname(os.path.abspath(__file__))

    # Go up one directory to the root folder
    root_directory = os.path.dirname(lib_directory)

    # Construct the path to the YAML file
    yaml_file_path = os.path.join(root_directory, "config.yaml")

    # Load the configuration from the YAML file
    with open(yaml_file_path, "r") as yaml_file:
        config = yaml.safe_load(yaml_file)
    
    return config