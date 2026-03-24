import os

JAR_FILES_PATH = "/home/quentin/Documents/Post-doc/Nicola/Eclipse_CDT/jars"
POM_PATH = "/home/quentin/IdeaProjects/ACSLParser/eclipse_cdt"
POM_FILE_NAME = "pom.xml"

def main():
    with open(os.path.join(POM_PATH, POM_FILE_NAME), "w") as pom_file:
        pom_file.write("<project>\n")
        pom_file.write("\t<modelVersion>4.0.0</modelVersion>\n\n")
        pom_file.write("\t<groupId>com.example</groupId>\n")
        pom_file.write("\t<artifactId>eclipse-cdt</artifactId>\n")
        pom_file.write("\t<version>1.0</version>\n")
        pom_file.write("\t<packaging>jar</packaging>\n\n")
        pom_file.write("\t<dependencies>")

        write_deps(pom_file)

        pom_file.write("\t</dependencies>\n")
        pom_file.write("</project>")

def write_deps(pom_file):
    for file in os.listdir(JAR_FILES_PATH):
        if file.endswith(".jar"):
            pom_file.write("\n\t\t<dependency>\n")
            pom_file.write("\t\t\t<groupId>local.libs</groupId>\n")
            pom_file.write("\t\t\t<artifactId>")
            pom_file.write(os.path.basename(file.replace(".jar", "")))
            pom_file.write("</artifactId>\n")
            pom_file.write("\t\t\t<version>1.0</version>\n")
            pom_file.write("\t\t</dependency>\n")

if __name__ == "__main__":
    main()
