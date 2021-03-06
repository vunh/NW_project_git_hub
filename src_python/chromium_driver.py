from urllib.parse import urlparse
import os
import shutil
import subprocess



class RunChromium:
    def __init__(self, site, orig_imp, profile):
        self.site = site
        self.profile = profile
        self.home_dir = '/home/vu'
        self.base_dir = '/home/vu/page_speed'
        self.d_chromium_path = self.home_dir + '/chrome/chrome'
        self.chromium_args = '--no-sandbox' + ' ' + '--user-data-dir'
        prelog_dir = self.base_dir + '/tests/analysis_t/pre_log'
        self.orig_site = 'http://original.testbed.localhost'
        self.improved_site = 'http://modified.testbed.localhost'
        self.orig_imp = orig_imp
        self.desktop_mobile = profile['device_type']

        if os.path.isdir(prelog_dir):
            for root, dirs, l_files in os.walk(prelog_dir):
                for f in l_files:
                    os.unlink(os.path.join(root, f))
                for d in dirs:
                    shutil.rmtree(os.path.join(root, d))
        else:
            os.makedirs(prelog_dir)

    def crawl(self, site):

        prelog_dir = self.base_dir + '/tests/analysis_t/pre_log'
        s1 = urlparse(self.site)
        if os.path.isdir(prelog_dir):
            for root, dirs, l_files in os.walk(prelog_dir):
                for f in l_files:
                    os.unlink(os.path.join(root, f))
                for d in dirs:
                    shutil.rmtree(os.path.join(root, d))
        else:
            os.makedirs(prelog_dir)
        result_file = os.path.join(prelog_dir, s1.netloc + '.txt')
        print('result_file',os.path.join(prelog_dir, s1.netloc + '.txt') )
        logfile = open(result_file, 'a+')  # append result to logfile

        if self.desktop_mobile == 'desktop':
            subprocess.call(['killall', 'chrome'], shell=False)
            netns_com = 'ip' + ' ' + 'netns' + ' ' + 'exec' + ' ' + 'client_tb' + ' '

            try:
                command = ''
                if self.orig_imp == 'orig':
                    command = netns_com + 'sudo' + ' ' + '-u' + ' ' + 'vu' + ' ' + self.d_chromium_path + ' ' + self.chromium_args + ' ' + self.orig_site + '/' + s1.netloc
                else:
                    command = netns_com + 'sudo' + ' ' + '-u' + ' ' + 'vu' + ' ' + self.d_chromium_path + ' ' + self.chromium_args + ' ' + self.improved_site + '/' + s1.netloc

                print("Wprof launched: " + site + ' command: ' + command)
                proc = subprocess.call(command.split(), env={"DISPLAY": "localhost:7"}, stderr=logfile, shell=False,
                                       timeout=50)
                # print (proc.returncode)
            except subprocess.TimeoutExpired:
                print("Killed process " + site + " after timeout")

            logfile.close()

        elif self.desktop_mobile == 'mobile':

            os.system('killall adb')
            os.system('killall bash.sh')
            loadcommand = './bash.sh'
            if self.orig_imp == 'orig':
                cur_site = self.orig_site + '/' + s1.netloc
            else:
                cur_site = self.improved_site + '/' + s1.netloc

            loadcommand = loadcommand + ' ' + cur_site + ' '
            loadcommand = loadcommand + ' ' + result_file + ' '
            print(loadcommand)
            try:
                proc1 = subprocess.call(loadcommand, shell=True, timeout=80)

            except subprocess.TimeoutExpired:
                print("Killed process " + site + " after timeout")

            logfile.close()

    def clear_cache(self):
        # Ubuntu by default does not have DNS cache service.
        # Clearing google chromium cache directory

        cache_dir = self.home_dir + '/.cache/chromium/Default/Cache/'
        for root, dirs, files in os.walk(cache_dir):
            for f in files:
                os.unlink(os.path.join(root, f))
            for d in dirs:
                shutil.rmtree(os.path.join(root, d))
        print('Cache directory cleared!\n')

        cache_dir = self.home_dir + '/.config/chromium/'
        for root, dirs, files in os.walk(cache_dir):
            for f in files:
                os.unlink(os.path.join(root, f))
            for d in dirs:
                shutil.rmtree(os.path.join(root, d))
        print('Cache directory cleared!\n')


    def clear_cookies(self):

        # command = 'sqlite3' + ' ' + '/home/jnejati/.config/chromium/Default/Cookies' + ' ' + "'DELETE FROM cookies;'"
        if self.profile['device_type'] == 'desktop':
            command = ["/home/jnejati/android-sdk-linux/platform-tools/sqlite3", "/home/vu/.config/chromium/Default/Cookies", "DELETE FROM cookies"]
        elif self.profile['device_type'] == 'mobile':
            command = ['echo', 'mobile']
        try:
            proc = subprocess.call(command, shell=False, timeout=30)
            print("Cookies cleared")
        except subprocess.TimeoutExpired:
            print("Killed process " + " after timeout")

    def main_run(self):
        #self.clear_cache()
        #self.clear_cookies()
        self.crawl(self.site)

