import requests
import ctypes
import json
import os
import sys


def read_file(path):
    with open(path, 'rb') as f:
        return f.read()

def write_file(str_content, path):
    with open(path, 'w') as f:
        f.write(str_content)


# 200
def post_json_to_server(post_url, account, data, files=None):
    #header = {"User-Agent": "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36"}
    #r = requests.post(post_url,data=json.dumps(data),auth=(account['name'],account['passwd']),headers = header)
    r = requests.post(post_url, data=data, files=files)
    print(r.content)
    print(r.status_code)

def request_json_to_server(request_url, account, data):
    r = requests.post(request_url, data=data)
    print(r.content)
    print(r.status_code)
    # print("654321")
    # print(r.text)
    # print("123456")
    path = ".\limits.json"
    write_file(r.text, path)

# 200
def get_from_server(get_url, account):
    response = requests.get(get_url,auth=(account['name'],account['passwd']))
    print(response.content)
    print("123456")

if __name__ == "__main__":
    
    if(len(sys.argv) > 1):
        station_info = sys.argv[1]
        print(sys.argv[0])
    else:
        print('Please check the correct grammer')

    get_url = "http://10.90.100.15:8101/api/1/ping"       # Connected successfully
    account = {'name': 'ambit', 'passwd': 'bento'}

    get_from_server(get_url,account)

    # request_url = "http://ambit:bento@10.90.100.15:8101/api/1/eden/results"
    # request_url = "http://ambit:bento@10.90.100.15:8101/api/1/eden/limits"
    # request_url = "http://ambit:bento@10.90.100.15:8101/api/1/eden/debug"
    # request_url = "http://ambit:bento@10.90.100.15:8101/api/1/Trieste/results"
    # request_url = "http://ambit:bento@10.90.100.15:8101/api/1/Trieste/limits"
    # request_url = "http://ambit:bento@10.90.100.15:8101/api/1/Trieste/debug"
    # request_url = "http://ambit:bento@10.90.100.15:8101/api/1/results"
    request_url = "http://ambit:bento@10.90.100.15:8101/api/1/limits"
    # request_url = "http://ambit:bento@10.90.100.15:8101/api/1/debug"

    data = {'station_type': station_info, "model": "TRIESTE"}

    request_json_to_server(request_url, account, data)
