#!/usr/bin/env python

from http.server import BaseHTTPRequestHandler, HTTPServer
import json
import random


class RequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.end_headers()
        self.wfile.write(
            bytes(
                json.dumps({'yourNumber': random.randint(1, 100)}),
                'UTF-8'
            )
        )


print('starting server on 8080')
server_address = ('127.0.0.1', 8080)
httpd = HTTPServer(server_address, RequestHandler)
httpd.serve_forever()

