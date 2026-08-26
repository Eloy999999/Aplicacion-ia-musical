export declare class StringUtil {
    static StringContainsSeparatedWord(str: string, wordRegExString: string, ignoreCase?: boolean): boolean;
    /**
     * Checks whether the entire string is the given word or phrase, i.e. doesn't just contain it within a longer text.
     * Trailing spaces and periods are allowed, e.g. "D.C. al Fine." still matches "d\.c\. al fine".
     * @param str the string to check. Should already be trimmed (no leading/trailing whitespace).
     * @param wordRegExString the word or phrase to check for, given as a regular expression string (input for new RegExp())
     * @param ignoreCase whether to match case-insensitively
     * @returns true if str is (only) the given word or phrase
     */
    static StringIsWord(str: string, wordRegExString: string, ignoreCase?: boolean): boolean;
}
