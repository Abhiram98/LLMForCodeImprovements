import json
import random
import numpy as np
import matplotlib.pyplot as plt
from collections import defaultdict


def main(write=False):
    myrand = random.Random(42)

    with open("../../../data/Java-Corpus.json") as f:
        corpus_data = json.load(f)

    all_stars = [int(corpus_data[i]["github"]["stars"]) for i in corpus_data]

    projects_by_stars = defaultdict(list)
    for d in corpus_data:
        stars = int(corpus_data[d]["github"]["stars"])
        if corpus_data[d]['systems']['build_types']['maven']\
                or corpus_data[d]['systems']['build_types']['gradle']:
            projects_by_stars[stars].append(d)

    print(f"{min(all_stars)=}")
    print(f"{max(all_stars)=}")
    print(f"{np.mean(all_stars)=}")

    # all_stars = [i if i < 200 else 200 for i in all_stars]
    # plt.hist(all_stars)
    # plt.show()

    num_projects = 20
    segments = np.arange(5, 1000, 1000//num_projects)
    all_selected_projects = []
    selected_dict = {}
    for i, s in enumerate(segments):
        upper_stars = segments[i+1]-1 if i+1 < len(segments) else 1000-1
        lower_stars = segments[i]
        print(f"{upper_stars=}")
        projects = []
        for j in range(lower_stars, upper_stars + 1):
            projects += projects_by_stars[j]
        print(f"{len(projects)=}")
        assert len(projects) > 0
        ind = myrand.randint(0, len(projects))
        selected_project = projects[ind]
        all_selected_projects.append({"name":selected_project,
                                      "stars": corpus_data[selected_project]["github"]["stars"]})

        selected_dict[selected_project] = corpus_data[selected_project]


    print(all_selected_projects)

    if write:
        with open(f'../../../data/selected_projects_{num_projects}.json', 'w') as f:
            json.dump(selected_dict, f, indent=1)





if __name__ == '__main__':
    main()
