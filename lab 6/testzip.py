import sys
import os
import zipfile
import shutil
import subprocess
from threading import Timer

zip_file = sys.argv[1]

l = len(zip_file.split("\\"))
print("Students :")
for i in range(0, len(zip_file.split("\\")[l - 1].split("_"))):
    print(zip_file.split("\\")[l - 1].split("_")[i].split(".")[0])
print("")

submissions_temp_dir = "./submissions/"

if not os.path.exists(submissions_temp_dir):
    os.mkdir(submissions_temp_dir)

zip_ref = zipfile.ZipFile(zip_file, 'r')
zip_ref.extractall(submissions_temp_dir)
zip_ref.close()

shutil.copyfile("build.xml", os.path.join(submissions_temp_dir, "build.xml"))

os.chdir(submissions_temp_dir)

stdout_file = open("./tmp.output", 'a')
popen_args = ["ant", "make-jar"]
proc = subprocess.Popen(popen_args, stdout=stdout_file, stderr=stdout_file, shell=True)
timer = Timer(5, proc.kill)
try:
    timer.start()
    stdout, stderr = proc.communicate()
finally:
    timer.cancel()
stdout_file.close()

if not os.path.exists("jars/simulator.jar"):
    print("Compilation failed. JAR file not created.")
    sys.exit(1)

test_cases_dir = "../test_cases"
total_marks = 0
scored_marks = 0
for testcase in os.listdir(test_cases_dir):
    if testcase.endswith(".out"):
        total_marks = total_marks + 1

        stdout_file = open("./" + testcase.split(".")[0] + ".observedoutput", 'w')
        popen_args = [
            "java", "-Xmx1g", "-jar", "jars/simulator.jar", "./src/configuration/config.xml",
            "./" + testcase.split(".")[0] + ".observedstat", os.path.join(test_cases_dir, testcase)
        ]
        # print(popen_args)
        proc = subprocess.Popen(popen_args, stdout=stdout_file, stderr=stdout_file, shell=True)
        timer = Timer(5, proc.kill)
        try:
            timer.start()
            stdout, stderr = proc.communicate()
        finally:
            timer.cancel()
        stdout_file.close()

        if os.path.exists("./" + testcase.split(".")[0] + ".observedoutput"):
            expectedoutput_file = open(os.path.join(test_cases_dir, testcase.split(".")[0] + ".expected"))
            expected_hash = expectedoutput_file.readline()
            expectedoutput_file.close()

            correct = False
            observedoutput_file = open("./" + testcase.split(".")[0] + ".observedoutput")
            for line in observedoutput_file:
                if line.strip() == expected_hash.strip():
                    correct = True
                    break
            observedoutput_file.close()
            if correct:
                scored_marks = scored_marks + 1
                print(testcase + " : PASS!")
            else:
                print(testcase + " : FAIL - Incorrect hash")
        else:
            print(testcase + " : FAIL - Standard output file not created")

os.chdir("..")

shutil.rmtree(submissions_temp_dir)

print("\nTotal score = " + str(scored_marks) + " out of " + str(total_marks))
