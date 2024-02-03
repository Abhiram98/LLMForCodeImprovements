import json
import os.path
import subprocess
import urllib.request

import sample_projects

def main():
    if not os.path.exists("../../../data/Java-Corpus.json"):
        corpus_url = "http://mondego.ics.uci.edu/projects/SourcererJBF/jbf-arti/Java-Corpus.json"
        urllib.request.urlretrieve(corpus_url, "../../../data/Java-Corpus.json")

    sample_projects.main(write=False)

    with open("../../../data/selected_projects_20.json") as f:
        selected_projects = json.load(f)

    for p in selected_projects:
        owner = selected_projects[p]['github']['owner']
        repo_name = selected_projects[p]['github']['repository']
        url = f'https://github.com/{owner}/{repo_name}.git'

        subprocess.run([
            "git", "clone",
            url, f"../../../data/projects/{repo_name}"
        ])



if __name__ == '__main__':
    main()
