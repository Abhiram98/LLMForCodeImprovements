from query_llm import query
import random
from collections import defaultdict, Counter
import json
import pandas as pd
import matplotlib.pyplot as plt


def prompt(improvement):
    prompt = f"""
    Your task is to classify a suggestion (within double qoutes) to improve a java method into one of the following classes:
    
    Use <method-1> method instead of <method-2>.
    Use StringBuilder instead of <>.
    Use try-with-resources.
    Improve variable name(s).
    Add comments.
    Use diamond operator.
    Use <method> instead of null check.*
    Use enhanced for loop.
    Improve code formatting.
    Use switch statement.
    Use Lambda expressions.
    Use Java streams.
    Extract code blocks into separate method.
    Improve Exception handling.
    Change Type.
    Replace Magic values with constants.
    Remove Statements/Expressions.
    Add Statements/Expressions.
    Other.
     
    
    
    "{improvement}"
    """

    return prompt


# def main():
#     with open("../../../data/improvements/descriptions.json") as f:
#         descriptions = json.load(f)
#
#     imps500 = random.sample(range(len(descriptions)), 200)
#
#     # classes = defaultdict(list)
#     classes = []
#     descs = []
#     long_desc = []
#     for i in imps500:
#         d = f"{descriptions[i]['desc']}. {descriptions[i]['long']}"
#         descs.append(descriptions[i]['desc'])
#         long_desc.append(descriptions[i]['long'])
#         classification = query(prompt(d), "", 0, "GPT-4")
#         # classes[classification].append(descriptions[i]['desc'])
#         classes.append(classification)
#     print(classes)
#
#     df = pd.DataFrame(zip(descs, long_desc, classes),
#                       columns=['Description', 'Details', 'Classification'])
#
#     df.to_csv('../../../data/improvements/classes2.csv', index=False)


def analyse():
    with open("../../../data/improvements/improvement_class.json") as f:
        improvements_data = json.load(f)

    seen = set()
    uniqueidlist = []
    for obj in improvements_data:
        attrs = (obj['filePath'], obj['startLine'], obj['endLine'])
        if attrs not in seen:
            seen.add(attrs)
            uniqueidlist.append(obj)
    improvements_data = uniqueidlist
    print(f"{len(improvements_data)=}")


    valid_classes = \
        ("Use <method-1> method instead of <method-2>.\n" +
         "Use StringBuilder instead of <>.\n" +
         "Use try-with-resources.\n" +
         "Improve variable name(s).\n" +
         "Add comments.\n" +
         "Use diamond operator.\n" +
         "Use <method> instead of null check.\n" +
         "Use enhanced for loop.\n" +
         "Improve code formatting.\n" +
         "Use switch statement.\n" +
         "Use Lambda expressions.\n" +
         "Use Java streams.\n" +
         "Extract code blocks into separate method.\n" +
         "Improve Exception handling.\n" +
         "Change Type.\n" +
         "Replace Magic values with constants.\n" +
         "Remove Statements/Expressions.\n" +
         "Add Statements/Expressions.\n" +
         "Move Statements/Expressions.\n" +
         "Use Ternary operator.\n" +
         "Add null checking condition.\n" +
         "Use final keyword for immutable variables.\n" +
         "Other.").split('\n')

    classes = [i['class'].strip() for i in improvements_data]
    classes_filtered = [i for i in classes if i in valid_classes]

    hist_data = Counter(classes_filtered)
    hd = sorted(zip(hist_data.keys(), hist_data.values()),
                key=lambda x: x[1])
    print([i for i in classes if i not in valid_classes])
    plt.barh([i[0] for i in hd], [i[1] for i in hd])
    plt.show()


def extract_sample():
    with open("../../../data/improvements/improvement_class.json") as f:
        improvements_data = json.load(f)

    other_imps = [i for i in improvements_data if i['class'].strip()=='Other.']
    print(f"{len(other_imps)=}")
    cols = ['longDescription', 'shortDescription', 'class']


    sample300 = random.sample(improvements_data, 300)
    df = pd.DataFrame(
        zip(*[[i[cname] for i in sample300] for cname in cols]),
        columns=cols
    )
    df.to_csv('../../../data/improvements/improvements_sample_300.csv', index=False)

    other_df = pd.DataFrame(
        zip(*[[i[cname] for i in other_imps] for cname in cols]),
        columns=cols
    )
    other_df.to_csv('../../../data/improvements/improvements_other.csv', index=False)



if __name__ == '__main__':
    analyse()
    extract_sample()
