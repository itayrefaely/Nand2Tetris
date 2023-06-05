# IP-to-ASN Mapper

This project provides a Python implementation for performing IP-to-ASN lookups. 
It includes a Trie data structure to efficiently store IP ranges and their corresponding ASN information, 
as well as a command-line interface for user interaction.

## Usage

1.	Ensure you have Python installed on your system.
2.	Download the project files and place them in a directory.
3.	Option 1: Using Your Own Data File\
    •	In the project directory, create a data file containing IP-to-ASN mapping data. Each line should follow the format:\
        ```
        start_ip end_ip asn country as_name
        ```\
        Here's an example line: \
        ```
        16777216 16777471 13335 US CLOUDFLARENET
        ```\
    •	In the main() function of the main.py file, specify the name of your data file by replacing 'ip2asn-v4-u32.tsv' in the line:\
        ```
        mapper = IPtoASNMapper('ip2asn-v4-u32.tsv')
        ```\
        with the name of your data file.
4.	Option 2: Using the Embedded Data File\
    •	If you want to use the embedded data file included in the project, no further configuration is needed.\
        The embedded data file is named 'ip2asn-v4-u32.tsv'.
5.	Open a terminal or command prompt and navigate to the project directory.
6.	Run the following command to execute the program: python main.py
7.	The program will prompt you to enter an IP address containing only digits. You can enter multiple IP addresses one after another.\
    •	To exit the program, type "q" and press Enter.\
    •	If you enter an invalid IP address or encounter an error, an error message will be displayed.
8.	The program will perform the IP-to-ASN lookup for each valid IP address and display the corresponding ASN.

## Example

Here are five examples of how the data should be formatted in the data file:
```
16777216    16777471    13335   US  CLOUDFLARENET
16777472    16778239    0       None    Not routed
16778240    16778751    38803   AU  WPL-AS-AP Wirefreebroadband Pty Ltd
16778752    16779263    38803   AU  WPL-AS-AP Wirefreebroadband Pty Ltd
16779264    16781311    0       None    Not routed
```
Ensure that your data file follows this format, with each line representing an IP range and its associated ASN information.

## Dependencies

The project has the following dependencies:\
•	Python 3.x\
•	No additional external libraries are required.

## Acknowledgements

The implementation of the IP-to-ASN mapping functionality in this project is based on the Trie data structure. \
The Trie implementation is adapted from the code provided in the trie.py file.

## Author

This project was developed by Itay Refaely.
