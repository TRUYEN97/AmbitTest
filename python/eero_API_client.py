import requests
import ctypes
import json
import os
import sys
def read_file(path):
    with open(path, 'rb') as f:
        return f.read()

# 200    
def post_json_to_server(post_url, account, data, files=None):
    #header = {"User-Agent": "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36"}
    #r = requests.post(post_url,data=json.dumps(data),auth=(account['name'],account['passwd']),headers = header)
    r = requests.post(post_url, data=data, files=files)
    print(r.content)
    print(r.status_code)

# 200    
def get_from_server(get_url, account):
    response = requests.get(get_url,auth=(account['name'],account['passwd']))
    print(response.content)
    #print(response.status_code)

if __name__ == "__main__":

    if(len(sys.argv) > 1):
        filename = str(sys.argv[1])
        zipFilename = filename + r'_serial.txt'
        imgFilename = filename + r'_image.zip'
        print(zipFilename)
       
    #results = read_file(path)
    get_url = "http://10.90.100.15:8101/api/1/ping"  # Connected successfully

    if os.path.exists(filename+".json"):
        results = read_file(filename+".json")
        post_url = "http://ambit:bento@10.90.100.15:8101/api/1/results"
    else:
        print("must have a json file")
        exit(0)
        
    #results = read_file('log.json')
    get_url = "http://10.90.100.15:8101/api/1/ping"  # Connected successfully
    account = {'name':'ambit', 'passwd':'bento'}

    data = {'run_results': results}
    get_from_server(get_url, account)
    temp_files = read_file(zipFilename)

    if 'BFT-' in str(zipFilename) or 'SRF-' in str(zipFilename):
        litepoint_str = 'log/litepoint.zip'
        litepoint_files = read_file(litepoint_str)
        files = {'serial.txt' : temp_files, 'litepoint.zip': litepoint_files}
    elif 'AGC-' in str(zipFilename):
        #imgFilename_str = r'log/' + imgFilename
        imgFiles = read_file(imgFilename)
        files = {'serial.txt' : temp_files, 'image.zip' : imgFiles}
    else:
        files = {'serial.txt' : temp_files}

    post_json_to_server(post_url, account, data, files)
