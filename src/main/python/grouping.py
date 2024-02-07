def prompt():
    prompt = """
    Your task is to classify a suggestion to improve a java method into one of the following classes:
    Use enhanced for loop.
    Use foreach loop instead of traditional for loop.
    Improve variable name(s).
    Extract code blocks into separate method.
    Use StringBuilder instead of.
    Use lambda expression.
    Use <idiom-1> instead of <idiom-2>.
    Use <method> instead of null check.*
    Use <method-1> method instead of <method-2>.
    Improve code formatting.
    Use try-with-resources.
    Use switch statement.
    Add comments.
    Use diamond operator.
    
    Return only the name of the final classification. If there is no appropriate match, return 'Other'.
    
    {improvement}
    """