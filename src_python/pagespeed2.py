__author__ = 'jnejati'

import experiments
import convert
import post_analyze
import apache_conf
import network_emulator
from urllib.parse import urlparse
import  chromium_driver
import time
import modifications as modify
from bs4 import BeautifulSoup
import urllib.request
import urllib.response
import io
import gzip
import subprocess


def main():
    input_file = 'mixed200.txt'
    #exp_type = 'compression'
    #exp_type = 'minification'
    exp_type = 'inline'
    arch_dir = '/home/vu/page_speed/arch_dir'
    my_profiles = [
                   # Wi-Fi ac
                   {'conn_type':'wifi-fast',
                  'device_type': 'desktop',
                  'page_type': 'top200',
                  'cache': 'no_cache',
                  'download_rate':'100Mbit',
                  'download_delay':'1ms',
                  'download_loss':'0.1%',
                  'upload_rate':'90Mbit',
                  'upload_delay':'1ms',
                  'upload_loss':'0.1%'}
                                        ]

    #apache_conf.rm_dir(arch_dir)
    #apache_conf.fetch_all_sites(input_file, arch_dir)
    for run_no in range(1):
        top_sites = (l.strip().split() for l in open("./res/" + input_file).read().splitlines())
        for site in top_sites:
            for net_profile in my_profiles:
                print('Current profile: ' + net_profile['device_type'] + ' - ' + net_profile['conn_type'])
                s1 = urlparse(site[0])
                apache_conf.rm_dir('/var/www/original.testbed.localhost')
                command = ["cp", "-R", arch_dir + '/' + s1.netloc + '/.', '/var/www/original.testbed.localhost']
                try:
                    proc = subprocess.call(command, shell=False, timeout=30)
                    print(s1.netloc + ' copied')
                except subprocess.TimeoutExpired:
                    print("Couldn't copy ", s1.netloc)
                apachectl_orig = apache_conf.ApacheConf(net_profile['device_type'], net_profile['conn_type'], input_file.split('.')[0], 'orig', '/home/jnejati')
                apachectl_modified = apache_conf.ApacheConf(net_profile['conn_type'], net_profile['conn_type'],  input_file.split('.')[0], exp_type, '/home/jnejati')

                apachectl_orig.a2ensite('original.testbed.localhost')
                apache_conf.ApacheConf.restart_apache()
                apachectl_modified.a2ensite('modified.testbed.localhost')
                apachectl_modified.restart_apache()
                apachectl_orig.initialize_archive_dir()
                try:
                    netns = network_emulator.NetworkEmulator(net_profile)
                    netns.set_profile(net_profile['conn_type'])
                    my_run = chromium_driver.RunChromium(site[0], 'orig',  net_profile)
                    my_run.main_run()
                    conv_orig = convert.Convert()
                    if conv_orig.do_analysis(net_profile, 'orig', exp_type):
                        print("Origin json file analyzed")
                        my_exp = experiments.SetExperiment(exp_type)
                        my_exp.run(site[0], net_profile)
                        conv_mod = convert.Convert()
                        conv_mod.do_analysis(net_profile, 'modified', exp_type)
                        apachectl_modified.archive_site(s1.netloc)

                    else:
                        print("No origin json file for this site")
                        break
                    print("Origin json file analyzed")
                
                    my_exp = experiments.SetExperiment(exp_type)
                    my_exp.run(site[0], net_profile)
                    conv_mod = convert.Convert()
                    conv_mod.do_analysis(net_profile, 'modified', exp_type)
                    #apachectl_modified.archive_site(s1.netloc)
                    #time.sleep(60)
                except Exception as e:
                    print("Error")

        try:
            for net_profile in my_profiles:
                post_analyze.run(net_profile, 'orig', exp_type)
        except Exception as e:
            print("Error")


if __name__ == '__main__':
    main()
