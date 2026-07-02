/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr;

import com.sk89q.worldedit.antlr.ExpressionListener;
import com.sk89q.worldedit.antlr.ExpressionVisitor;
import com.sk89q.worldedit.antlr4.runtime.FailedPredicateException;
import com.sk89q.worldedit.antlr4.runtime.NoViableAltException;
import com.sk89q.worldedit.antlr4.runtime.Parser;
import com.sk89q.worldedit.antlr4.runtime.ParserRuleContext;
import com.sk89q.worldedit.antlr4.runtime.RecognitionException;
import com.sk89q.worldedit.antlr4.runtime.RuleContext;
import com.sk89q.worldedit.antlr4.runtime.RuntimeMetaData;
import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.antlr4.runtime.TokenStream;
import com.sk89q.worldedit.antlr4.runtime.Vocabulary;
import com.sk89q.worldedit.antlr4.runtime.VocabularyImpl;
import com.sk89q.worldedit.antlr4.runtime.atn.ATN;
import com.sk89q.worldedit.antlr4.runtime.atn.ATNDeserializer;
import com.sk89q.worldedit.antlr4.runtime.atn.ParserATNSimulator;
import com.sk89q.worldedit.antlr4.runtime.atn.PredictionContextCache;
import com.sk89q.worldedit.antlr4.runtime.dfa.DFA;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTreeListener;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTreeVisitor;
import com.sk89q.worldedit.antlr4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.List;

public class ExpressionParser
extends Parser {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int TIMES = 3;
    public static final int DIVIDE = 4;
    public static final int REMAINDER = 5;
    public static final int POWER = 6;
    public static final int LEFT_SHIFT = 7;
    public static final int RIGHT_SHIFT = 8;
    public static final int ASSIGN = 9;
    public static final int COMPLEMENT = 10;
    public static final int PLUS_ASSIGN = 11;
    public static final int MINUS_ASSIGN = 12;
    public static final int TIMES_ASSIGN = 13;
    public static final int DIVIDE_ASSIGN = 14;
    public static final int REMAINDER_ASSIGN = 15;
    public static final int POWER_ASSIGN = 16;
    public static final int EQUAL = 17;
    public static final int NOT_EQUAL = 18;
    public static final int NEAR = 19;
    public static final int LESS_THAN = 20;
    public static final int LESS_THAN_OR_EQUAL = 21;
    public static final int GREATER_THAN = 22;
    public static final int GREATER_THAN_OR_EQUAL = 23;
    public static final int AND_SC = 24;
    public static final int OR_SC = 25;
    public static final int INCREMENT = 26;
    public static final int DECREMENT = 27;
    public static final int COMMA = 28;
    public static final int OPEN_PAREN = 29;
    public static final int CLOSE_PAREN = 30;
    public static final int OPEN_BRACKET = 31;
    public static final int CLOSE_BRACKET = 32;
    public static final int SEMI_COLON = 33;
    public static final int QUESTION_MARK = 34;
    public static final int COLON = 35;
    public static final int EXCLAMATION_MARK = 36;
    public static final int IF = 37;
    public static final int ELSE = 38;
    public static final int WHILE = 39;
    public static final int DO = 40;
    public static final int FOR = 41;
    public static final int BREAK = 42;
    public static final int CONTINUE = 43;
    public static final int RETURN = 44;
    public static final int SWITCH = 45;
    public static final int CASE = 46;
    public static final int DEFAULT = 47;
    public static final int NUMBER = 48;
    public static final int ID = 49;
    public static final int WS = 50;
    public static final int RULE_allStatements = 0;
    public static final int RULE_statements = 1;
    public static final int RULE_statement = 2;
    public static final int RULE_block = 3;
    public static final int RULE_ifStatement = 4;
    public static final int RULE_whileStatement = 5;
    public static final int RULE_doStatement = 6;
    public static final int RULE_forStatement = 7;
    public static final int RULE_simpleForStatement = 8;
    public static final int RULE_breakStatement = 9;
    public static final int RULE_continueStatement = 10;
    public static final int RULE_returnStatement = 11;
    public static final int RULE_switchStatement = 12;
    public static final int RULE_switchLabel = 13;
    public static final int RULE_expressionStatement = 14;
    public static final int RULE_emptyStatement = 15;
    public static final int RULE_expression = 16;
    public static final int RULE_assignmentExpression = 17;
    public static final int RULE_assignment = 18;
    public static final int RULE_assignmentOperator = 19;
    public static final int RULE_conditionalExpression = 20;
    public static final int RULE_conditionalOrExpression = 21;
    public static final int RULE_conditionalAndExpression = 22;
    public static final int RULE_equalityExpression = 23;
    public static final int RULE_relationalExpression = 24;
    public static final int RULE_shiftExpression = 25;
    public static final int RULE_additiveExpression = 26;
    public static final int RULE_multiplicativeExpression = 27;
    public static final int RULE_powerExpression = 28;
    public static final int RULE_unaryExpression = 29;
    public static final int RULE_postfixExpression = 30;
    public static final int RULE_unprioritizedExpression = 31;
    public static final int RULE_constantExpression = 32;
    public static final int RULE_functionCall = 33;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0004\u00012\u0149\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0004\u0001I\b\u0001\u000b\u0001\f\u0001J\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002Y\b\u0002\u0001\u0002\u0003\u0002\\\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004i\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0004\f\u009c\b\f\u000b\f\f\f\u009d\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0003\r\u00a5\b\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0003\u0011\u00af\b\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u00be\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u00c6\b\u0015\n\u0015\f\u0015\u00c9\t\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u00d1\b\u0016\n\u0016\f\u0016\u00d4\t\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0005\u0017\u00dc\b\u0017\n\u0017\f\u0017\u00df\t\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0005\u0018\u00e7\b\u0018\n\u0018\f\u0018\u00ea\t\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0005\u0019\u00f2\b\u0019\n\u0019\f\u0019\u00f5\t\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a\u00fd\b\u001a\n\u001a\f\u001a\u0100\t\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0005\u001b\u0108\b\u001b\n\u001b\f\u001b\u010b\t\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c\u0113\b\u001c\n\u001c\f\u001c\u0116\t\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0121\b\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u0127\b\u001e\u0001\u001e\u0001\u001e\u0005\u001e\u012b\b\u001e\n\u001e\f\u001e\u012e\t\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u0137\b\u001f\u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0001!\u0005!\u0140\b!\n!\f!\u0143\t!\u0003!\u0145\b!\u0001!\u0001!\u0001!\u0000\t*,.02468<\"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@B\u0000\u0007\u0002\u0000\t\t\u000b\u0010\u0001\u0000\u0011\u0013\u0001\u0000\u0014\u0017\u0001\u0000\u0007\b\u0001\u0000\u0001\u0002\u0001\u0000\u0003\u0005\u0001\u0000\u001a\u001b\u014b\u0000D\u0001\u0000\u0000\u0000\u0002H\u0001\u0000\u0000\u0000\u0004X\u0001\u0000\u0000\u0000\u0006]\u0001\u0000\u0000\u0000\ba\u0001\u0000\u0000\u0000\nj\u0001\u0000\u0000\u0000\fp\u0001\u0000\u0000\u0000\u000ew\u0001\u0000\u0000\u0000\u0010\u0081\u0001\u0000\u0000\u0000\u0012\u008b\u0001\u0000\u0000\u0000\u0014\u008d\u0001\u0000\u0000\u0000\u0016\u008f\u0001\u0000\u0000\u0000\u0018\u0092\u0001\u0000\u0000\u0000\u001a\u00a4\u0001\u0000\u0000\u0000\u001c\u00a6\u0001\u0000\u0000\u0000\u001e\u00a8\u0001\u0000\u0000\u0000 \u00aa\u0001\u0000\u0000\u0000\"\u00ae\u0001\u0000\u0000\u0000$\u00b0\u0001\u0000\u0000\u0000&\u00b4\u0001\u0000\u0000\u0000(\u00bd\u0001\u0000\u0000\u0000*\u00bf\u0001\u0000\u0000\u0000,\u00ca\u0001\u0000\u0000\u0000.\u00d5\u0001\u0000\u0000\u00000\u00e0\u0001\u0000\u0000\u00002\u00eb\u0001\u0000\u0000\u00004\u00f6\u0001\u0000\u0000\u00006\u0101\u0001\u0000\u0000\u00008\u010c\u0001\u0000\u0000\u0000:\u0120\u0001\u0000\u0000\u0000<\u0126\u0001\u0000\u0000\u0000>\u0136\u0001\u0000\u0000\u0000@\u0138\u0001\u0000\u0000\u0000B\u013a\u0001\u0000\u0000\u0000DE\u0003\u0002\u0001\u0000EF\u0005\u0000\u0000\u0001F\u0001\u0001\u0000\u0000\u0000GI\u0003\u0004\u0002\u0000HG\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JH\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000K\u0003\u0001\u0000\u0000\u0000LY\u0003\u0006\u0003\u0000MY\u0003\b\u0004\u0000NY\u0003\n\u0005\u0000OY\u0003\f\u0006\u0000PY\u0003\u000e\u0007\u0000QY\u0003\u0010\b\u0000RY\u0003\u0012\t\u0000SY\u0003\u0014\n\u0000TY\u0003\u0016\u000b\u0000UY\u0003\u0018\f\u0000VY\u0003\u001c\u000e\u0000WY\u0003\u001e\u000f\u0000XL\u0001\u0000\u0000\u0000XM\u0001\u0000\u0000\u0000XN\u0001\u0000\u0000\u0000XO\u0001\u0000\u0000\u0000XP\u0001\u0000\u0000\u0000XQ\u0001\u0000\u0000\u0000XR\u0001\u0000\u0000\u0000XS\u0001\u0000\u0000\u0000XT\u0001\u0000\u0000\u0000XU\u0001\u0000\u0000\u0000XV\u0001\u0000\u0000\u0000XW\u0001\u0000\u0000\u0000Y[\u0001\u0000\u0000\u0000Z\\\u0005!\u0000\u0000[Z\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\\u0005\u0001\u0000\u0000\u0000]^\u0005\u001f\u0000\u0000^_\u0003\u0002\u0001\u0000_`\u0005 \u0000\u0000`\u0007\u0001\u0000\u0000\u0000ab\u0005%\u0000\u0000bc\u0005\u001d\u0000\u0000cd\u0003 \u0010\u0000de\u0005\u001e\u0000\u0000eh\u0003\u0004\u0002\u0000fg\u0005&\u0000\u0000gi\u0003\u0004\u0002\u0000hf\u0001\u0000\u0000\u0000hi\u0001\u0000\u0000\u0000i\t\u0001\u0000\u0000\u0000jk\u0005'\u0000\u0000kl\u0005\u001d\u0000\u0000lm\u0003 \u0010\u0000mn\u0005\u001e\u0000\u0000no\u0003\u0004\u0002\u0000o\u000b\u0001\u0000\u0000\u0000pq\u0005(\u0000\u0000qr\u0003\u0004\u0002\u0000rs\u0005'\u0000\u0000st\u0005\u001d\u0000\u0000tu\u0003 \u0010\u0000uv\u0005\u001e\u0000\u0000v\r\u0001\u0000\u0000\u0000wx\u0005)\u0000\u0000xy\u0005\u001d\u0000\u0000yz\u0003 \u0010\u0000z{\u0005!\u0000\u0000{|\u0003 \u0010\u0000|}\u0005!\u0000\u0000}~\u0003 \u0010\u0000~\u007f\u0005\u001e\u0000\u0000\u007f\u0080\u0003\u0004\u0002\u0000\u0080\u000f\u0001\u0000\u0000\u0000\u0081\u0082\u0005)\u0000\u0000\u0082\u0083\u0005\u001d\u0000\u0000\u0083\u0084\u00051\u0000\u0000\u0084\u0085\u0005\t\u0000\u0000\u0085\u0086\u0003 \u0010\u0000\u0086\u0087\u0005\u001c\u0000\u0000\u0087\u0088\u0003 \u0010\u0000\u0088\u0089\u0005\u001e\u0000\u0000\u0089\u008a\u0003\u0004\u0002\u0000\u008a\u0011\u0001\u0000\u0000\u0000\u008b\u008c\u0005*\u0000\u0000\u008c\u0013\u0001\u0000\u0000\u0000\u008d\u008e\u0005+\u0000\u0000\u008e\u0015\u0001\u0000\u0000\u0000\u008f\u0090\u0005,\u0000\u0000\u0090\u0091\u0003 \u0010\u0000\u0091\u0017\u0001\u0000\u0000\u0000\u0092\u0093\u0005-\u0000\u0000\u0093\u0094\u0005\u001d\u0000\u0000\u0094\u0095\u0003 \u0010\u0000\u0095\u0096\u0005\u001e\u0000\u0000\u0096\u009b\u0005\u001f\u0000\u0000\u0097\u0098\u0003\u001a\r\u0000\u0098\u0099\u0005#\u0000\u0000\u0099\u009a\u0003\u0002\u0001\u0000\u009a\u009c\u0001\u0000\u0000\u0000\u009b\u0097\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0005 \u0000\u0000\u00a0\u0019\u0001\u0000\u0000\u0000\u00a1\u00a2\u0005.\u0000\u0000\u00a2\u00a5\u0003@ \u0000\u00a3\u00a5\u0005/\u0000\u0000\u00a4\u00a1\u0001\u0000\u0000\u0000\u00a4\u00a3\u0001\u0000\u0000\u0000\u00a5\u001b\u0001\u0000\u0000\u0000\u00a6\u00a7\u0003 \u0010\u0000\u00a7\u001d\u0001\u0000\u0000\u0000\u00a8\u00a9\u0005!\u0000\u0000\u00a9\u001f\u0001\u0000\u0000\u0000\u00aa\u00ab\u0003\"\u0011\u0000\u00ab!\u0001\u0000\u0000\u0000\u00ac\u00af\u0003(\u0014\u0000\u00ad\u00af\u0003$\u0012\u0000\u00ae\u00ac\u0001\u0000\u0000\u0000\u00ae\u00ad\u0001\u0000\u0000\u0000\u00af#\u0001\u0000\u0000\u0000\u00b0\u00b1\u00051\u0000\u0000\u00b1\u00b2\u0003&\u0013\u0000\u00b2\u00b3\u0003 \u0010\u0000\u00b3%\u0001\u0000\u0000\u0000\u00b4\u00b5\u0007\u0000\u0000\u0000\u00b5'\u0001\u0000\u0000\u0000\u00b6\u00be\u0003*\u0015\u0000\u00b7\u00b8\u0003*\u0015\u0000\u00b8\u00b9\u0005\"\u0000\u0000\u00b9\u00ba\u0003 \u0010\u0000\u00ba\u00bb\u0005#\u0000\u0000\u00bb\u00bc\u0003(\u0014\u0000\u00bc\u00be\u0001\u0000\u0000\u0000\u00bd\u00b6\u0001\u0000\u0000\u0000\u00bd\u00b7\u0001\u0000\u0000\u0000\u00be)\u0001\u0000\u0000\u0000\u00bf\u00c0\u0006\u0015\uffff\uffff\u0000\u00c0\u00c1\u0003,\u0016\u0000\u00c1\u00c7\u0001\u0000\u0000\u0000\u00c2\u00c3\n\u0001\u0000\u0000\u00c3\u00c4\u0005\u0019\u0000\u0000\u00c4\u00c6\u0003,\u0016\u0000\u00c5\u00c2\u0001\u0000\u0000\u0000\u00c6\u00c9\u0001\u0000\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000\u0000\u0000\u00c8+\u0001\u0000\u0000\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00ca\u00cb\u0006\u0016\uffff\uffff\u0000\u00cb\u00cc\u0003.\u0017\u0000\u00cc\u00d2\u0001\u0000\u0000\u0000\u00cd\u00ce\n\u0001\u0000\u0000\u00ce\u00cf\u0005\u0018\u0000\u0000\u00cf\u00d1\u0003.\u0017\u0000\u00d0\u00cd\u0001\u0000\u0000\u0000\u00d1\u00d4\u0001\u0000\u0000\u0000\u00d2\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3-\u0001\u0000\u0000\u0000\u00d4\u00d2\u0001\u0000\u0000\u0000\u00d5\u00d6\u0006\u0017\uffff\uffff\u0000\u00d6\u00d7\u00030\u0018\u0000\u00d7\u00dd\u0001\u0000\u0000\u0000\u00d8\u00d9\n\u0001\u0000\u0000\u00d9\u00da\u0007\u0001\u0000\u0000\u00da\u00dc\u00030\u0018\u0000\u00db\u00d8\u0001\u0000\u0000\u0000\u00dc\u00df\u0001\u0000\u0000\u0000\u00dd\u00db\u0001\u0000\u0000\u0000\u00dd\u00de\u0001\u0000\u0000\u0000\u00de/\u0001\u0000\u0000\u0000\u00df\u00dd\u0001\u0000\u0000\u0000\u00e0\u00e1\u0006\u0018\uffff\uffff\u0000\u00e1\u00e2\u00032\u0019\u0000\u00e2\u00e8\u0001\u0000\u0000\u0000\u00e3\u00e4\n\u0001\u0000\u0000\u00e4\u00e5\u0007\u0002\u0000\u0000\u00e5\u00e7\u00032\u0019\u0000\u00e6\u00e3\u0001\u0000\u0000\u0000\u00e7\u00ea\u0001\u0000\u0000\u0000\u00e8\u00e6\u0001\u0000\u0000\u0000\u00e8\u00e9\u0001\u0000\u0000\u0000\u00e91\u0001\u0000\u0000\u0000\u00ea\u00e8\u0001\u0000\u0000\u0000\u00eb\u00ec\u0006\u0019\uffff\uffff\u0000\u00ec\u00ed\u00034\u001a\u0000\u00ed\u00f3\u0001\u0000\u0000\u0000\u00ee\u00ef\n\u0001\u0000\u0000\u00ef\u00f0\u0007\u0003\u0000\u0000\u00f0\u00f2\u00034\u001a\u0000\u00f1\u00ee\u0001\u0000\u0000\u0000\u00f2\u00f5\u0001\u0000\u0000\u0000\u00f3\u00f1\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000\u00f43\u0001\u0000\u0000\u0000\u00f5\u00f3\u0001\u0000\u0000\u0000\u00f6\u00f7\u0006\u001a\uffff\uffff\u0000\u00f7\u00f8\u00036\u001b\u0000\u00f8\u00fe\u0001\u0000\u0000\u0000\u00f9\u00fa\n\u0001\u0000\u0000\u00fa\u00fb\u0007\u0004\u0000\u0000\u00fb\u00fd\u00036\u001b\u0000\u00fc\u00f9\u0001\u0000\u0000\u0000\u00fd\u0100\u0001\u0000\u0000\u0000\u00fe\u00fc\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000\u0000\u00ff5\u0001\u0000\u0000\u0000\u0100\u00fe\u0001\u0000\u0000\u0000\u0101\u0102\u0006\u001b\uffff\uffff\u0000\u0102\u0103\u00038\u001c\u0000\u0103\u0109\u0001\u0000\u0000\u0000\u0104\u0105\n\u0001\u0000\u0000\u0105\u0106\u0007\u0005\u0000\u0000\u0106\u0108\u00038\u001c\u0000\u0107\u0104\u0001\u0000\u0000\u0000\u0108\u010b\u0001\u0000\u0000\u0000\u0109\u0107\u0001\u0000\u0000\u0000\u0109\u010a\u0001\u0000\u0000\u0000\u010a7\u0001\u0000\u0000\u0000\u010b\u0109\u0001\u0000\u0000\u0000\u010c\u010d\u0006\u001c\uffff\uffff\u0000\u010d\u010e\u0003:\u001d\u0000\u010e\u0114\u0001\u0000\u0000\u0000\u010f\u0110\n\u0001\u0000\u0000\u0110\u0111\u0005\u0006\u0000\u0000\u0111\u0113\u0003:\u001d\u0000\u0112\u010f\u0001\u0000\u0000\u0000\u0113\u0116\u0001\u0000\u0000\u0000\u0114\u0112\u0001\u0000\u0000\u0000\u0114\u0115\u0001\u0000\u0000\u0000\u01159\u0001\u0000\u0000\u0000\u0116\u0114\u0001\u0000\u0000\u0000\u0117\u0118\u0007\u0006\u0000\u0000\u0118\u0121\u00051\u0000\u0000\u0119\u011a\u0007\u0004\u0000\u0000\u011a\u0121\u0003:\u001d\u0000\u011b\u0121\u0003<\u001e\u0000\u011c\u011d\u0005\n\u0000\u0000\u011d\u0121\u0003:\u001d\u0000\u011e\u011f\u0005$\u0000\u0000\u011f\u0121\u0003:\u001d\u0000\u0120\u0117\u0001\u0000\u0000\u0000\u0120\u0119\u0001\u0000\u0000\u0000\u0120\u011b\u0001\u0000\u0000\u0000\u0120\u011c\u0001\u0000\u0000\u0000\u0120\u011e\u0001\u0000\u0000\u0000\u0121;\u0001\u0000\u0000\u0000\u0122\u0123\u0006\u001e\uffff\uffff\u0000\u0123\u0127\u0003>\u001f\u0000\u0124\u0125\u00051\u0000\u0000\u0125\u0127\u0007\u0006\u0000\u0000\u0126\u0122\u0001\u0000\u0000\u0000\u0126\u0124\u0001\u0000\u0000\u0000\u0127\u012c\u0001\u0000\u0000\u0000\u0128\u0129\n\u0001\u0000\u0000\u0129\u012b\u0005$\u0000\u0000\u012a\u0128\u0001\u0000\u0000\u0000\u012b\u012e\u0001\u0000\u0000\u0000\u012c\u012a\u0001\u0000\u0000\u0000\u012c\u012d\u0001\u0000\u0000\u0000\u012d=\u0001\u0000\u0000\u0000\u012e\u012c\u0001\u0000\u0000\u0000\u012f\u0137\u0003B!\u0000\u0130\u0137\u0003@ \u0000\u0131\u0137\u00051\u0000\u0000\u0132\u0133\u0005\u001d\u0000\u0000\u0133\u0134\u0003 \u0010\u0000\u0134\u0135\u0005\u001e\u0000\u0000\u0135\u0137\u0001\u0000\u0000\u0000\u0136\u012f\u0001\u0000\u0000\u0000\u0136\u0130\u0001\u0000\u0000\u0000\u0136\u0131\u0001\u0000\u0000\u0000\u0136\u0132\u0001\u0000\u0000\u0000\u0137?\u0001\u0000\u0000\u0000\u0138\u0139\u00050\u0000\u0000\u0139A\u0001\u0000\u0000\u0000\u013a\u013b\u00051\u0000\u0000\u013b\u0144\u0005\u001d\u0000\u0000\u013c\u0141\u0003 \u0010\u0000\u013d\u013e\u0005\u001c\u0000\u0000\u013e\u0140\u0003 \u0010\u0000\u013f\u013d\u0001\u0000\u0000\u0000\u0140\u0143\u0001\u0000\u0000\u0000\u0141\u013f\u0001\u0000\u0000\u0000\u0141\u0142\u0001\u0000\u0000\u0000\u0142\u0145\u0001\u0000\u0000\u0000\u0143\u0141\u0001\u0000\u0000\u0000\u0144\u013c\u0001\u0000\u0000\u0000\u0144\u0145\u0001\u0000\u0000\u0000\u0145\u0146\u0001\u0000\u0000\u0000\u0146\u0147\u0005\u001e\u0000\u0000\u0147C\u0001\u0000\u0000\u0000\u0016JX[h\u009d\u00a4\u00ae\u00bd\u00c7\u00d2\u00dd\u00e8\u00f3\u00fe\u0109\u0114\u0120\u0126\u012c\u0136\u0141\u0144";
    public static final ATN _ATN;

    private static String[] makeRuleNames() {
        return new String[]{"allStatements", "statements", "statement", "block", "ifStatement", "whileStatement", "doStatement", "forStatement", "simpleForStatement", "breakStatement", "continueStatement", "returnStatement", "switchStatement", "switchLabel", "expressionStatement", "emptyStatement", "expression", "assignmentExpression", "assignment", "assignmentOperator", "conditionalExpression", "conditionalOrExpression", "conditionalAndExpression", "equalityExpression", "relationalExpression", "shiftExpression", "additiveExpression", "multiplicativeExpression", "powerExpression", "unaryExpression", "postfixExpression", "unprioritizedExpression", "constantExpression", "functionCall"};
    }

    private static String[] makeLiteralNames() {
        return new String[]{null, "'+'", "'-'", "'*'", "'/'", "'%'", null, "'<<'", "'>>'", "'='", "'~'", "'+='", "'-='", "'*='", "'/='", "'%='", "'^='", "'=='", "'!='", "'~='", "'<'", "'<='", "'>'", "'>='", "'&&'", "'||'", "'++'", "'--'", "','", "'('", "')'", "'{'", "'}'", "';'", "'?'", "':'", "'!'", "'if'", "'else'", "'while'", "'do'", "'for'", "'break'", "'continue'", "'return'", "'switch'", "'case'", "'default'"};
    }

    private static String[] makeSymbolicNames() {
        return new String[]{null, "PLUS", "MINUS", "TIMES", "DIVIDE", "REMAINDER", "POWER", "LEFT_SHIFT", "RIGHT_SHIFT", "ASSIGN", "COMPLEMENT", "PLUS_ASSIGN", "MINUS_ASSIGN", "TIMES_ASSIGN", "DIVIDE_ASSIGN", "REMAINDER_ASSIGN", "POWER_ASSIGN", "EQUAL", "NOT_EQUAL", "NEAR", "LESS_THAN", "LESS_THAN_OR_EQUAL", "GREATER_THAN", "GREATER_THAN_OR_EQUAL", "AND_SC", "OR_SC", "INCREMENT", "DECREMENT", "COMMA", "OPEN_PAREN", "CLOSE_PAREN", "OPEN_BRACKET", "CLOSE_BRACKET", "SEMI_COLON", "QUESTION_MARK", "COLON", "EXCLAMATION_MARK", "IF", "ELSE", "WHILE", "DO", "FOR", "BREAK", "CONTINUE", "RETURN", "SWITCH", "CASE", "DEFAULT", "NUMBER", "ID", "WS"};
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override
    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public String getGrammarFileName() {
        return "Expression.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public ExpressionParser(TokenStream input) {
        super(input);
        this._interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public final AllStatementsContext allStatements() throws RecognitionException {
        AllStatementsContext _localctx = new AllStatementsContext(this._ctx, this.getState());
        this.enterRule(_localctx, 0, 0);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(68);
            this.statements();
            this.setState(69);
            this.match(-1);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final StatementsContext statements() throws RecognitionException {
        StatementsContext _localctx = new StatementsContext(this._ctx, this.getState());
        this.enterRule(_localctx, 2, 1);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(72);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            do {
                this.setState(71);
                this.statement();
                this.setState(74);
                this._errHandler.sync(this);
            } while (((_la = this._input.LA(1)) & 0xFFFFFFC0) == 0 && (1L << _la & 0x33FB2AC000406L) != 0L);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final StatementContext statement() throws RecognitionException {
        StatementContext _localctx = new StatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 4, 2);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(88);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 1, this._ctx)) {
                case 1: {
                    this.setState(76);
                    this.block();
                    break;
                }
                case 2: {
                    this.setState(77);
                    this.ifStatement();
                    break;
                }
                case 3: {
                    this.setState(78);
                    this.whileStatement();
                    break;
                }
                case 4: {
                    this.setState(79);
                    this.doStatement();
                    break;
                }
                case 5: {
                    this.setState(80);
                    this.forStatement();
                    break;
                }
                case 6: {
                    this.setState(81);
                    this.simpleForStatement();
                    break;
                }
                case 7: {
                    this.setState(82);
                    this.breakStatement();
                    break;
                }
                case 8: {
                    this.setState(83);
                    this.continueStatement();
                    break;
                }
                case 9: {
                    this.setState(84);
                    this.returnStatement();
                    break;
                }
                case 10: {
                    this.setState(85);
                    this.switchStatement();
                    break;
                }
                case 11: {
                    this.setState(86);
                    this.expressionStatement();
                    break;
                }
                case 12: {
                    this.setState(87);
                    this.emptyStatement();
                    break;
                }
            }
            this.setState(91);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 2, this._ctx)) {
                case 1: {
                    this.setState(90);
                    this.match(33);
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final BlockContext block() throws RecognitionException {
        BlockContext _localctx = new BlockContext(this._ctx, this.getState());
        this.enterRule(_localctx, 6, 3);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(93);
            this.match(31);
            this.setState(94);
            this.statements();
            this.setState(95);
            this.match(32);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final IfStatementContext ifStatement() throws RecognitionException {
        IfStatementContext _localctx = new IfStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 8, 4);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(97);
            this.match(37);
            this.setState(98);
            this.match(29);
            this.setState(99);
            _localctx.condition = this.expression();
            this.setState(100);
            this.match(30);
            this.setState(101);
            _localctx.trueBranch = this.statement();
            this.setState(104);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 3, this._ctx)) {
                case 1: {
                    this.setState(102);
                    this.match(38);
                    this.setState(103);
                    _localctx.falseBranch = this.statement();
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final WhileStatementContext whileStatement() throws RecognitionException {
        WhileStatementContext _localctx = new WhileStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 10, 5);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(106);
            this.match(39);
            this.setState(107);
            this.match(29);
            this.setState(108);
            _localctx.condition = this.expression();
            this.setState(109);
            this.match(30);
            this.setState(110);
            _localctx.body = this.statement();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final DoStatementContext doStatement() throws RecognitionException {
        DoStatementContext _localctx = new DoStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 12, 6);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(112);
            this.match(40);
            this.setState(113);
            _localctx.body = this.statement();
            this.setState(114);
            this.match(39);
            this.setState(115);
            this.match(29);
            this.setState(116);
            _localctx.condition = this.expression();
            this.setState(117);
            this.match(30);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final ForStatementContext forStatement() throws RecognitionException {
        ForStatementContext _localctx = new ForStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 14, 7);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(119);
            this.match(41);
            this.setState(120);
            this.match(29);
            this.setState(121);
            _localctx.init = this.expression();
            this.setState(122);
            this.match(33);
            this.setState(123);
            _localctx.condition = this.expression();
            this.setState(124);
            this.match(33);
            this.setState(125);
            _localctx.update = this.expression();
            this.setState(126);
            this.match(30);
            this.setState(127);
            _localctx.body = this.statement();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final SimpleForStatementContext simpleForStatement() throws RecognitionException {
        SimpleForStatementContext _localctx = new SimpleForStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 16, 8);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(129);
            this.match(41);
            this.setState(130);
            this.match(29);
            this.setState(131);
            _localctx.counter = this.match(49);
            this.setState(132);
            this.match(9);
            this.setState(133);
            _localctx.first = this.expression();
            this.setState(134);
            this.match(28);
            this.setState(135);
            _localctx.last = this.expression();
            this.setState(136);
            this.match(30);
            this.setState(137);
            _localctx.body = this.statement();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final BreakStatementContext breakStatement() throws RecognitionException {
        BreakStatementContext _localctx = new BreakStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 18, 9);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(139);
            this.match(42);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final ContinueStatementContext continueStatement() throws RecognitionException {
        ContinueStatementContext _localctx = new ContinueStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 20, 10);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(141);
            this.match(43);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final ReturnStatementContext returnStatement() throws RecognitionException {
        ReturnStatementContext _localctx = new ReturnStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 22, 11);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(143);
            this.match(44);
            this.setState(144);
            _localctx.value = this.expression();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final SwitchStatementContext switchStatement() throws RecognitionException {
        SwitchStatementContext _localctx = new SwitchStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 24, 12);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(146);
            this.match(45);
            this.setState(147);
            this.match(29);
            this.setState(148);
            _localctx.target = this.expression();
            this.setState(149);
            this.match(30);
            this.setState(150);
            this.match(31);
            this.setState(155);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            do {
                this.setState(151);
                _localctx.switchLabel = this.switchLabel();
                _localctx.labels.add(_localctx.switchLabel);
                this.setState(152);
                this.match(35);
                this.setState(153);
                _localctx.statements = this.statements();
                _localctx.bodies.add(_localctx.statements);
                this.setState(157);
                this._errHandler.sync(this);
            } while ((_la = this._input.LA(1)) == 46 || _la == 47);
            this.setState(159);
            this.match(32);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final SwitchLabelContext switchLabel() throws RecognitionException {
        SwitchLabelContext _localctx = new SwitchLabelContext(this._ctx, this.getState());
        this.enterRule(_localctx, 26, 13);
        try {
            this.setState(164);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 46: {
                    _localctx = new CaseContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(161);
                    this.match(46);
                    this.setState(162);
                    ((CaseContext)_localctx).constant = this.constantExpression();
                    return _localctx;
                }
                case 47: {
                    _localctx = new DefaultContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(163);
                    this.match(47);
                    return _localctx;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final ExpressionStatementContext expressionStatement() throws RecognitionException {
        ExpressionStatementContext _localctx = new ExpressionStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 28, 14);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(166);
            this.expression();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final EmptyStatementContext emptyStatement() throws RecognitionException {
        EmptyStatementContext _localctx = new EmptyStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 30, 15);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(168);
            this.match(33);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final ExpressionContext expression() throws RecognitionException {
        ExpressionContext _localctx = new ExpressionContext(this._ctx, this.getState());
        this.enterRule(_localctx, 32, 16);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(170);
            this.assignmentExpression();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final AssignmentExpressionContext assignmentExpression() throws RecognitionException {
        AssignmentExpressionContext _localctx = new AssignmentExpressionContext(this._ctx, this.getState());
        this.enterRule(_localctx, 34, 17);
        try {
            this.setState(174);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 6, this._ctx)) {
                case 1: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(172);
                    this.conditionalExpression();
                    return _localctx;
                }
                case 2: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(173);
                    this.assignment();
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final AssignmentContext assignment() throws RecognitionException {
        AssignmentContext _localctx = new AssignmentContext(this._ctx, this.getState());
        this.enterRule(_localctx, 36, 18);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(176);
            _localctx.target = this.match(49);
            this.setState(177);
            this.assignmentOperator();
            this.setState(178);
            _localctx.source = this.expression();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final AssignmentOperatorContext assignmentOperator() throws RecognitionException {
        AssignmentOperatorContext _localctx = new AssignmentOperatorContext(this._ctx, this.getState());
        this.enterRule(_localctx, 38, 19);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(180);
            int _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0x1FA00L) == 0L) {
                this._errHandler.recoverInline(this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch(this);
                this.consume();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final ConditionalExpressionContext conditionalExpression() throws RecognitionException {
        ConditionalExpressionContext _localctx = new ConditionalExpressionContext(this._ctx, this.getState());
        this.enterRule(_localctx, 40, 20);
        try {
            this.setState(189);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 7, this._ctx)) {
                case 1: {
                    _localctx = new CEFallthroughContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(182);
                    this.conditionalOrExpression(0);
                    return _localctx;
                }
                case 2: {
                    _localctx = new TernaryExprContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(183);
                    ((TernaryExprContext)_localctx).condition = this.conditionalOrExpression(0);
                    this.setState(184);
                    this.match(34);
                    this.setState(185);
                    ((TernaryExprContext)_localctx).trueBranch = this.expression();
                    this.setState(186);
                    this.match(35);
                    this.setState(187);
                    ((TernaryExprContext)_localctx).falseBranch = this.conditionalExpression();
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final ConditionalOrExpressionContext conditionalOrExpression() throws RecognitionException {
        return this.conditionalOrExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ConditionalOrExpressionContext conditionalOrExpression(int _p) throws RecognitionException {
        ConditionalOrExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        ConditionalOrExpressionContext _prevctx = _localctx = new ConditionalOrExpressionContext(this._ctx, _parentState);
        int _startState = 42;
        this.enterRecursionRule(_localctx, 42, 21, _p);
        try {
            this.enterOuterAlt(_localctx, 1);
            _localctx = new COFallthroughContext(_localctx);
            this._ctx = _localctx;
            _prevctx = _localctx;
            this.setState(192);
            this.conditionalAndExpression(0);
            this._ctx.stop = this._input.LT(-1);
            this.setState(199);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 8, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    _localctx = new ConditionalOrExprContext(new ConditionalOrExpressionContext(_parentctx, _parentState));
                    ((ConditionalOrExprContext)_localctx).left = _prevctx;
                    this.pushNewRecursionContext(_localctx, _startState, 21);
                    this.setState(194);
                    if (!this.precpred(this._ctx, 1)) {
                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    }
                    this.setState(195);
                    this.match(25);
                    this.setState(196);
                    ((ConditionalOrExprContext)_localctx).right = this.conditionalAndExpression(0);
                }
                this.setState(201);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 8, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public final ConditionalAndExpressionContext conditionalAndExpression() throws RecognitionException {
        return this.conditionalAndExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ConditionalAndExpressionContext conditionalAndExpression(int _p) throws RecognitionException {
        ConditionalAndExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        ConditionalAndExpressionContext _prevctx = _localctx = new ConditionalAndExpressionContext(this._ctx, _parentState);
        int _startState = 44;
        this.enterRecursionRule(_localctx, 44, 22, _p);
        try {
            this.enterOuterAlt(_localctx, 1);
            _localctx = new CAFallthroughContext(_localctx);
            this._ctx = _localctx;
            _prevctx = _localctx;
            this.setState(203);
            this.equalityExpression(0);
            this._ctx.stop = this._input.LT(-1);
            this.setState(210);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 9, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    _localctx = new ConditionalAndExprContext(new ConditionalAndExpressionContext(_parentctx, _parentState));
                    ((ConditionalAndExprContext)_localctx).left = _prevctx;
                    this.pushNewRecursionContext(_localctx, _startState, 22);
                    this.setState(205);
                    if (!this.precpred(this._ctx, 1)) {
                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    }
                    this.setState(206);
                    this.match(24);
                    this.setState(207);
                    ((ConditionalAndExprContext)_localctx).right = this.equalityExpression(0);
                }
                this.setState(212);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 9, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public final EqualityExpressionContext equalityExpression() throws RecognitionException {
        return this.equalityExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private EqualityExpressionContext equalityExpression(int _p) throws RecognitionException {
        EqualityExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        EqualityExpressionContext _prevctx = _localctx = new EqualityExpressionContext(this._ctx, _parentState);
        int _startState = 46;
        this.enterRecursionRule(_localctx, 46, 23, _p);
        try {
            this.enterOuterAlt(_localctx, 1);
            _localctx = new EqFallthroughContext(_localctx);
            this._ctx = _localctx;
            _prevctx = _localctx;
            this.setState(214);
            this.relationalExpression(0);
            this._ctx.stop = this._input.LT(-1);
            this.setState(221);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 10, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    _localctx = new EqualityExprContext(new EqualityExpressionContext(_parentctx, _parentState));
                    ((EqualityExprContext)_localctx).left = _prevctx;
                    this.pushNewRecursionContext(_localctx, _startState, 23);
                    this.setState(216);
                    if (!this.precpred(this._ctx, 1)) {
                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    }
                    this.setState(217);
                    ((EqualityExprContext)_localctx).op = this._input.LT(1);
                    int _la = this._input.LA(1);
                    if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0xE0000L) == 0L) {
                        ((EqualityExprContext)_localctx).op = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(218);
                    ((EqualityExprContext)_localctx).right = this.relationalExpression(0);
                }
                this.setState(223);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 10, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public final RelationalExpressionContext relationalExpression() throws RecognitionException {
        return this.relationalExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private RelationalExpressionContext relationalExpression(int _p) throws RecognitionException {
        RelationalExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        RelationalExpressionContext _prevctx = _localctx = new RelationalExpressionContext(this._ctx, _parentState);
        int _startState = 48;
        this.enterRecursionRule(_localctx, 48, 24, _p);
        try {
            this.enterOuterAlt(_localctx, 1);
            _localctx = new ReFallthroughContext(_localctx);
            this._ctx = _localctx;
            _prevctx = _localctx;
            this.setState(225);
            this.shiftExpression(0);
            this._ctx.stop = this._input.LT(-1);
            this.setState(232);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 11, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    _localctx = new RelationalExprContext(new RelationalExpressionContext(_parentctx, _parentState));
                    ((RelationalExprContext)_localctx).left = _prevctx;
                    this.pushNewRecursionContext(_localctx, _startState, 24);
                    this.setState(227);
                    if (!this.precpred(this._ctx, 1)) {
                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    }
                    this.setState(228);
                    ((RelationalExprContext)_localctx).op = this._input.LT(1);
                    int _la = this._input.LA(1);
                    if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0xF00000L) == 0L) {
                        ((RelationalExprContext)_localctx).op = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(229);
                    ((RelationalExprContext)_localctx).right = this.shiftExpression(0);
                }
                this.setState(234);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 11, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public final ShiftExpressionContext shiftExpression() throws RecognitionException {
        return this.shiftExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ShiftExpressionContext shiftExpression(int _p) throws RecognitionException {
        ShiftExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        ShiftExpressionContext _prevctx = _localctx = new ShiftExpressionContext(this._ctx, _parentState);
        int _startState = 50;
        this.enterRecursionRule(_localctx, 50, 25, _p);
        try {
            this.enterOuterAlt(_localctx, 1);
            _localctx = new ShFallthroughContext(_localctx);
            this._ctx = _localctx;
            _prevctx = _localctx;
            this.setState(236);
            this.additiveExpression(0);
            this._ctx.stop = this._input.LT(-1);
            this.setState(243);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 12, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    _localctx = new ShiftExprContext(new ShiftExpressionContext(_parentctx, _parentState));
                    ((ShiftExprContext)_localctx).left = _prevctx;
                    this.pushNewRecursionContext(_localctx, _startState, 25);
                    this.setState(238);
                    if (!this.precpred(this._ctx, 1)) {
                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    }
                    this.setState(239);
                    ((ShiftExprContext)_localctx).op = this._input.LT(1);
                    int _la = this._input.LA(1);
                    if (_la != 7 && _la != 8) {
                        ((ShiftExprContext)_localctx).op = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(240);
                    ((ShiftExprContext)_localctx).right = this.additiveExpression(0);
                }
                this.setState(245);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 12, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public final AdditiveExpressionContext additiveExpression() throws RecognitionException {
        return this.additiveExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private AdditiveExpressionContext additiveExpression(int _p) throws RecognitionException {
        AdditiveExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        AdditiveExpressionContext _prevctx = _localctx = new AdditiveExpressionContext(this._ctx, _parentState);
        int _startState = 52;
        this.enterRecursionRule(_localctx, 52, 26, _p);
        try {
            this.enterOuterAlt(_localctx, 1);
            _localctx = new AdFallthroughContext(_localctx);
            this._ctx = _localctx;
            _prevctx = _localctx;
            this.setState(247);
            this.multiplicativeExpression(0);
            this._ctx.stop = this._input.LT(-1);
            this.setState(254);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 13, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    _localctx = new AddExprContext(new AdditiveExpressionContext(_parentctx, _parentState));
                    ((AddExprContext)_localctx).left = _prevctx;
                    this.pushNewRecursionContext(_localctx, _startState, 26);
                    this.setState(249);
                    if (!this.precpred(this._ctx, 1)) {
                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    }
                    this.setState(250);
                    ((AddExprContext)_localctx).op = this._input.LT(1);
                    int _la = this._input.LA(1);
                    if (_la != 1 && _la != 2) {
                        ((AddExprContext)_localctx).op = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(251);
                    ((AddExprContext)_localctx).right = this.multiplicativeExpression(0);
                }
                this.setState(256);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 13, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public final MultiplicativeExpressionContext multiplicativeExpression() throws RecognitionException {
        return this.multiplicativeExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private MultiplicativeExpressionContext multiplicativeExpression(int _p) throws RecognitionException {
        MultiplicativeExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        MultiplicativeExpressionContext _prevctx = _localctx = new MultiplicativeExpressionContext(this._ctx, _parentState);
        int _startState = 54;
        this.enterRecursionRule(_localctx, 54, 27, _p);
        try {
            this.enterOuterAlt(_localctx, 1);
            _localctx = new MuFallthroughContext(_localctx);
            this._ctx = _localctx;
            _prevctx = _localctx;
            this.setState(258);
            this.powerExpression(0);
            this._ctx.stop = this._input.LT(-1);
            this.setState(265);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 14, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    _localctx = new MultiplicativeExprContext(new MultiplicativeExpressionContext(_parentctx, _parentState));
                    ((MultiplicativeExprContext)_localctx).left = _prevctx;
                    this.pushNewRecursionContext(_localctx, _startState, 27);
                    this.setState(260);
                    if (!this.precpred(this._ctx, 1)) {
                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    }
                    this.setState(261);
                    ((MultiplicativeExprContext)_localctx).op = this._input.LT(1);
                    int _la = this._input.LA(1);
                    if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0x38L) == 0L) {
                        ((MultiplicativeExprContext)_localctx).op = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(262);
                    ((MultiplicativeExprContext)_localctx).right = this.powerExpression(0);
                }
                this.setState(267);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 14, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public final PowerExpressionContext powerExpression() throws RecognitionException {
        return this.powerExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PowerExpressionContext powerExpression(int _p) throws RecognitionException {
        PowerExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        PowerExpressionContext _prevctx = _localctx = new PowerExpressionContext(this._ctx, _parentState);
        int _startState = 56;
        this.enterRecursionRule(_localctx, 56, 28, _p);
        try {
            this.enterOuterAlt(_localctx, 1);
            _localctx = new PwFallthroughContext(_localctx);
            this._ctx = _localctx;
            _prevctx = _localctx;
            this.setState(269);
            this.unaryExpression();
            this._ctx.stop = this._input.LT(-1);
            this.setState(276);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 15, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    _localctx = new PowerExprContext(new PowerExpressionContext(_parentctx, _parentState));
                    ((PowerExprContext)_localctx).left = _prevctx;
                    this.pushNewRecursionContext(_localctx, _startState, 28);
                    this.setState(271);
                    if (!this.precpred(this._ctx, 1)) {
                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    }
                    this.setState(272);
                    this.match(6);
                    this.setState(273);
                    ((PowerExprContext)_localctx).right = this.unaryExpression();
                }
                this.setState(278);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 15, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final UnaryExpressionContext unaryExpression() throws RecognitionException {
        UnaryExpressionContext _localctx = new UnaryExpressionContext(this._ctx, this.getState());
        this.enterRule(_localctx, 58, 29);
        try {
            this.setState(288);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 26: 
                case 27: {
                    _localctx = new PreCrementExprContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(279);
                    ((PreCrementExprContext)_localctx).op = this._input.LT(1);
                    int _la = this._input.LA(1);
                    if (_la != 26 && _la != 27) {
                        ((PreCrementExprContext)_localctx).op = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(280);
                    ((PreCrementExprContext)_localctx).target = this.match(49);
                    return _localctx;
                }
                case 1: 
                case 2: {
                    _localctx = new PlusMinusExprContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(281);
                    ((PlusMinusExprContext)_localctx).op = this._input.LT(1);
                    int _la = this._input.LA(1);
                    if (_la != 1 && _la != 2) {
                        ((PlusMinusExprContext)_localctx).op = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(282);
                    ((PlusMinusExprContext)_localctx).expr = this.unaryExpression();
                    return _localctx;
                }
                case 29: 
                case 48: 
                case 49: {
                    _localctx = new UaFallthroughContext(_localctx);
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(283);
                    this.postfixExpression(0);
                    return _localctx;
                }
                case 10: {
                    _localctx = new ComplementExprContext(_localctx);
                    this.enterOuterAlt(_localctx, 4);
                    this.setState(284);
                    this.match(10);
                    this.setState(285);
                    ((ComplementExprContext)_localctx).expr = this.unaryExpression();
                    return _localctx;
                }
                case 36: {
                    _localctx = new NotExprContext(_localctx);
                    this.enterOuterAlt(_localctx, 5);
                    this.setState(286);
                    this.match(36);
                    this.setState(287);
                    ((NotExprContext)_localctx).expr = this.unaryExpression();
                    return _localctx;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final PostfixExpressionContext postfixExpression() throws RecognitionException {
        return this.postfixExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PostfixExpressionContext postfixExpression(int _p) throws RecognitionException {
        PostfixExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        PostfixExpressionContext _prevctx = _localctx = new PostfixExpressionContext(this._ctx, _parentState);
        int _startState = 60;
        this.enterRecursionRule(_localctx, 60, 30, _p);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(294);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 17, this._ctx)) {
                case 1: {
                    _localctx = new PoFallthroughContext(_localctx);
                    this._ctx = _localctx;
                    _prevctx = _localctx;
                    this.setState(291);
                    this.unprioritizedExpression();
                    break;
                }
                case 2: {
                    _localctx = new PostCrementExprContext(_localctx);
                    this._ctx = _localctx;
                    _prevctx = _localctx;
                    this.setState(292);
                    ((PostCrementExprContext)_localctx).target = this.match(49);
                    this.setState(293);
                    ((PostCrementExprContext)_localctx).op = this._input.LT(1);
                    int _la = this._input.LA(1);
                    if (_la != 26 && _la != 27) {
                        ((PostCrementExprContext)_localctx).op = this._errHandler.recoverInline(this);
                        break;
                    }
                    if (this._input.LA(1) == -1) {
                        this.matchedEOF = true;
                    }
                    this._errHandler.reportMatch(this);
                    this.consume();
                }
            }
            this._ctx.stop = this._input.LT(-1);
            this.setState(300);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 18, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    _localctx = new PostfixExprContext(new PostfixExpressionContext(_parentctx, _parentState));
                    ((PostfixExprContext)_localctx).expr = _prevctx;
                    this.pushNewRecursionContext(_localctx, _startState, 30);
                    this.setState(296);
                    if (!this.precpred(this._ctx, 1)) {
                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    }
                    this.setState(297);
                    ((PostfixExprContext)_localctx).op = this.match(36);
                }
                this.setState(302);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 18, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final UnprioritizedExpressionContext unprioritizedExpression() throws RecognitionException {
        UnprioritizedExpressionContext _localctx = new UnprioritizedExpressionContext(this._ctx, this.getState());
        this.enterRule(_localctx, 62, 31);
        try {
            this.setState(310);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 19, this._ctx)) {
                case 1: {
                    _localctx = new FunctionCallExprContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(303);
                    this.functionCall();
                    return _localctx;
                }
                case 2: {
                    _localctx = new ConstantExprContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(304);
                    this.constantExpression();
                    return _localctx;
                }
                case 3: {
                    _localctx = new IdExprContext(_localctx);
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(305);
                    ((IdExprContext)_localctx).source = this.match(49);
                    return _localctx;
                }
                case 4: {
                    _localctx = new WrappedExprContext(_localctx);
                    this.enterOuterAlt(_localctx, 4);
                    this.setState(306);
                    this.match(29);
                    this.setState(307);
                    this.expression();
                    this.setState(308);
                    this.match(30);
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final ConstantExpressionContext constantExpression() throws RecognitionException {
        ConstantExpressionContext _localctx = new ConstantExpressionContext(this._ctx, this.getState());
        this.enterRule(_localctx, 64, 32);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(312);
            this.match(48);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final FunctionCallContext functionCall() throws RecognitionException {
        FunctionCallContext _localctx = new FunctionCallContext(this._ctx, this.getState());
        this.enterRule(_localctx, 66, 33);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(314);
            _localctx.name = this.match(49);
            this.setState(315);
            this.match(29);
            this.setState(324);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x300102C000406L) != 0L) {
                this.setState(316);
                _localctx.expression = this.expression();
                _localctx.args.add(_localctx.expression);
                this.setState(321);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
                while (_la == 28) {
                    this.setState(317);
                    this.match(28);
                    this.setState(318);
                    _localctx.expression = this.expression();
                    _localctx.args.add(_localctx.expression);
                    this.setState(323);
                    this._errHandler.sync(this);
                    _la = this._input.LA(1);
                }
            }
            this.setState(326);
            this.match(30);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    @Override
    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 21: {
                return this.conditionalOrExpression_sempred((ConditionalOrExpressionContext)_localctx, predIndex);
            }
            case 22: {
                return this.conditionalAndExpression_sempred((ConditionalAndExpressionContext)_localctx, predIndex);
            }
            case 23: {
                return this.equalityExpression_sempred((EqualityExpressionContext)_localctx, predIndex);
            }
            case 24: {
                return this.relationalExpression_sempred((RelationalExpressionContext)_localctx, predIndex);
            }
            case 25: {
                return this.shiftExpression_sempred((ShiftExpressionContext)_localctx, predIndex);
            }
            case 26: {
                return this.additiveExpression_sempred((AdditiveExpressionContext)_localctx, predIndex);
            }
            case 27: {
                return this.multiplicativeExpression_sempred((MultiplicativeExpressionContext)_localctx, predIndex);
            }
            case 28: {
                return this.powerExpression_sempred((PowerExpressionContext)_localctx, predIndex);
            }
            case 30: {
                return this.postfixExpression_sempred((PostfixExpressionContext)_localctx, predIndex);
            }
        }
        return true;
    }

    private boolean conditionalOrExpression_sempred(ConditionalOrExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0: {
                return this.precpred(this._ctx, 1);
            }
        }
        return true;
    }

    private boolean conditionalAndExpression_sempred(ConditionalAndExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 1: {
                return this.precpred(this._ctx, 1);
            }
        }
        return true;
    }

    private boolean equalityExpression_sempred(EqualityExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 2: {
                return this.precpred(this._ctx, 1);
            }
        }
        return true;
    }

    private boolean relationalExpression_sempred(RelationalExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 3: {
                return this.precpred(this._ctx, 1);
            }
        }
        return true;
    }

    private boolean shiftExpression_sempred(ShiftExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 4: {
                return this.precpred(this._ctx, 1);
            }
        }
        return true;
    }

    private boolean additiveExpression_sempred(AdditiveExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 5: {
                return this.precpred(this._ctx, 1);
            }
        }
        return true;
    }

    private boolean multiplicativeExpression_sempred(MultiplicativeExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 6: {
                return this.precpred(this._ctx, 1);
            }
        }
        return true;
    }

    private boolean powerExpression_sempred(PowerExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 7: {
                return this.precpred(this._ctx, 1);
            }
        }
        return true;
    }

    private boolean postfixExpression_sempred(PostfixExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 8: {
                return this.precpred(this._ctx, 1);
            }
        }
        return true;
    }

    static {
        int i;
        RuntimeMetaData.checkVersion("4.13.1", "4.13.1");
        _sharedContextCache = new PredictionContextCache();
        ruleNames = ExpressionParser.makeRuleNames();
        _LITERAL_NAMES = ExpressionParser.makeLiteralNames();
        _SYMBOLIC_NAMES = ExpressionParser.makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (i = 0; i < tokenNames.length; ++i) {
            ExpressionParser.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                ExpressionParser.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            ExpressionParser.tokenNames[i] = "<INVALID>";
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
            ExpressionParser._decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public static class AllStatementsContext
    extends ParserRuleContext {
        public StatementsContext statements() {
            return this.getRuleContext(StatementsContext.class, 0);
        }

        public TerminalNode EOF() {
            return this.getToken(-1, 0);
        }

        public AllStatementsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 0;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterAllStatements(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitAllStatements(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitAllStatements(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class StatementsContext
    extends ParserRuleContext {
        public List<StatementContext> statement() {
            return this.getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return this.getRuleContext(StatementContext.class, i);
        }

        public StatementsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 1;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterStatements(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitStatements(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitStatements(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class StatementContext
    extends ParserRuleContext {
        public BlockContext block() {
            return this.getRuleContext(BlockContext.class, 0);
        }

        public IfStatementContext ifStatement() {
            return this.getRuleContext(IfStatementContext.class, 0);
        }

        public WhileStatementContext whileStatement() {
            return this.getRuleContext(WhileStatementContext.class, 0);
        }

        public DoStatementContext doStatement() {
            return this.getRuleContext(DoStatementContext.class, 0);
        }

        public ForStatementContext forStatement() {
            return this.getRuleContext(ForStatementContext.class, 0);
        }

        public SimpleForStatementContext simpleForStatement() {
            return this.getRuleContext(SimpleForStatementContext.class, 0);
        }

        public BreakStatementContext breakStatement() {
            return this.getRuleContext(BreakStatementContext.class, 0);
        }

        public ContinueStatementContext continueStatement() {
            return this.getRuleContext(ContinueStatementContext.class, 0);
        }

        public ReturnStatementContext returnStatement() {
            return this.getRuleContext(ReturnStatementContext.class, 0);
        }

        public SwitchStatementContext switchStatement() {
            return this.getRuleContext(SwitchStatementContext.class, 0);
        }

        public ExpressionStatementContext expressionStatement() {
            return this.getRuleContext(ExpressionStatementContext.class, 0);
        }

        public EmptyStatementContext emptyStatement() {
            return this.getRuleContext(EmptyStatementContext.class, 0);
        }

        public TerminalNode SEMI_COLON() {
            return this.getToken(33, 0);
        }

        public StatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 2;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class BlockContext
    extends ParserRuleContext {
        public TerminalNode OPEN_BRACKET() {
            return this.getToken(31, 0);
        }

        public StatementsContext statements() {
            return this.getRuleContext(StatementsContext.class, 0);
        }

        public TerminalNode CLOSE_BRACKET() {
            return this.getToken(32, 0);
        }

        public BlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 3;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterBlock(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitBlock(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitBlock(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class IfStatementContext
    extends ParserRuleContext {
        public ExpressionContext condition;
        public StatementContext trueBranch;
        public StatementContext falseBranch;

        public TerminalNode IF() {
            return this.getToken(37, 0);
        }

        public TerminalNode OPEN_PAREN() {
            return this.getToken(29, 0);
        }

        public TerminalNode CLOSE_PAREN() {
            return this.getToken(30, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public List<StatementContext> statement() {
            return this.getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return this.getRuleContext(StatementContext.class, i);
        }

        public TerminalNode ELSE() {
            return this.getToken(38, 0);
        }

        public IfStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 4;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterIfStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitIfStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitIfStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class WhileStatementContext
    extends ParserRuleContext {
        public ExpressionContext condition;
        public StatementContext body;

        public TerminalNode WHILE() {
            return this.getToken(39, 0);
        }

        public TerminalNode OPEN_PAREN() {
            return this.getToken(29, 0);
        }

        public TerminalNode CLOSE_PAREN() {
            return this.getToken(30, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public StatementContext statement() {
            return this.getRuleContext(StatementContext.class, 0);
        }

        public WhileStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 5;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterWhileStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitWhileStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitWhileStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class DoStatementContext
    extends ParserRuleContext {
        public StatementContext body;
        public ExpressionContext condition;

        public TerminalNode DO() {
            return this.getToken(40, 0);
        }

        public TerminalNode WHILE() {
            return this.getToken(39, 0);
        }

        public TerminalNode OPEN_PAREN() {
            return this.getToken(29, 0);
        }

        public TerminalNode CLOSE_PAREN() {
            return this.getToken(30, 0);
        }

        public StatementContext statement() {
            return this.getRuleContext(StatementContext.class, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public DoStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 6;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterDoStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitDoStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitDoStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ForStatementContext
    extends ParserRuleContext {
        public ExpressionContext init;
        public ExpressionContext condition;
        public ExpressionContext update;
        public StatementContext body;

        public TerminalNode FOR() {
            return this.getToken(41, 0);
        }

        public TerminalNode OPEN_PAREN() {
            return this.getToken(29, 0);
        }

        public List<TerminalNode> SEMI_COLON() {
            return this.getTokens(33);
        }

        public TerminalNode SEMI_COLON(int i) {
            return this.getToken(33, i);
        }

        public TerminalNode CLOSE_PAREN() {
            return this.getToken(30, 0);
        }

        public List<ExpressionContext> expression() {
            return this.getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return this.getRuleContext(ExpressionContext.class, i);
        }

        public StatementContext statement() {
            return this.getRuleContext(StatementContext.class, 0);
        }

        public ForStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 7;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterForStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitForStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitForStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class SimpleForStatementContext
    extends ParserRuleContext {
        public Token counter;
        public ExpressionContext first;
        public ExpressionContext last;
        public StatementContext body;

        public TerminalNode FOR() {
            return this.getToken(41, 0);
        }

        public TerminalNode OPEN_PAREN() {
            return this.getToken(29, 0);
        }

        public TerminalNode ASSIGN() {
            return this.getToken(9, 0);
        }

        public TerminalNode COMMA() {
            return this.getToken(28, 0);
        }

        public TerminalNode CLOSE_PAREN() {
            return this.getToken(30, 0);
        }

        public TerminalNode ID() {
            return this.getToken(49, 0);
        }

        public List<ExpressionContext> expression() {
            return this.getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return this.getRuleContext(ExpressionContext.class, i);
        }

        public StatementContext statement() {
            return this.getRuleContext(StatementContext.class, 0);
        }

        public SimpleForStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 8;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterSimpleForStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitSimpleForStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitSimpleForStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class BreakStatementContext
    extends ParserRuleContext {
        public TerminalNode BREAK() {
            return this.getToken(42, 0);
        }

        public BreakStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 9;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterBreakStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitBreakStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitBreakStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ContinueStatementContext
    extends ParserRuleContext {
        public TerminalNode CONTINUE() {
            return this.getToken(43, 0);
        }

        public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 10;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterContinueStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitContinueStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitContinueStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ReturnStatementContext
    extends ParserRuleContext {
        public ExpressionContext value;

        public TerminalNode RETURN() {
            return this.getToken(44, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 11;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterReturnStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitReturnStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitReturnStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class SwitchStatementContext
    extends ParserRuleContext {
        public ExpressionContext target;
        public SwitchLabelContext switchLabel;
        public List<SwitchLabelContext> labels = new ArrayList<SwitchLabelContext>();
        public StatementsContext statements;
        public List<StatementsContext> bodies = new ArrayList<StatementsContext>();

        public TerminalNode SWITCH() {
            return this.getToken(45, 0);
        }

        public TerminalNode OPEN_PAREN() {
            return this.getToken(29, 0);
        }

        public TerminalNode CLOSE_PAREN() {
            return this.getToken(30, 0);
        }

        public TerminalNode OPEN_BRACKET() {
            return this.getToken(31, 0);
        }

        public TerminalNode CLOSE_BRACKET() {
            return this.getToken(32, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public List<TerminalNode> COLON() {
            return this.getTokens(35);
        }

        public TerminalNode COLON(int i) {
            return this.getToken(35, i);
        }

        public List<SwitchLabelContext> switchLabel() {
            return this.getRuleContexts(SwitchLabelContext.class);
        }

        public SwitchLabelContext switchLabel(int i) {
            return this.getRuleContext(SwitchLabelContext.class, i);
        }

        public List<StatementsContext> statements() {
            return this.getRuleContexts(StatementsContext.class);
        }

        public StatementsContext statements(int i) {
            return this.getRuleContext(StatementsContext.class, i);
        }

        public SwitchStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 12;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterSwitchStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitSwitchStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitSwitchStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ExpressionStatementContext
    extends ParserRuleContext {
        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public ExpressionStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 14;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterExpressionStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitExpressionStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitExpressionStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class EmptyStatementContext
    extends ParserRuleContext {
        public TerminalNode SEMI_COLON() {
            return this.getToken(33, 0);
        }

        public EmptyStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 15;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterEmptyStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitEmptyStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitEmptyStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ExpressionContext
    extends ParserRuleContext {
        public AssignmentExpressionContext assignmentExpression() {
            return this.getRuleContext(AssignmentExpressionContext.class, 0);
        }

        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 16;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class SwitchLabelContext
    extends ParserRuleContext {
        public SwitchLabelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 13;
        }

        public SwitchLabelContext() {
        }

        public void copyFrom(SwitchLabelContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class CaseContext
    extends SwitchLabelContext {
        public ConstantExpressionContext constant;

        public TerminalNode CASE() {
            return this.getToken(46, 0);
        }

        public ConstantExpressionContext constantExpression() {
            return this.getRuleContext(ConstantExpressionContext.class, 0);
        }

        public CaseContext(SwitchLabelContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterCase(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitCase(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitCase(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ConstantExpressionContext
    extends ParserRuleContext {
        public TerminalNode NUMBER() {
            return this.getToken(48, 0);
        }

        public ConstantExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 32;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterConstantExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitConstantExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitConstantExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class DefaultContext
    extends SwitchLabelContext {
        public TerminalNode DEFAULT() {
            return this.getToken(47, 0);
        }

        public DefaultContext(SwitchLabelContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterDefault(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitDefault(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitDefault(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class AssignmentExpressionContext
    extends ParserRuleContext {
        public ConditionalExpressionContext conditionalExpression() {
            return this.getRuleContext(ConditionalExpressionContext.class, 0);
        }

        public AssignmentContext assignment() {
            return this.getRuleContext(AssignmentContext.class, 0);
        }

        public AssignmentExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 17;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterAssignmentExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitAssignmentExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitAssignmentExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ConditionalExpressionContext
    extends ParserRuleContext {
        public ConditionalExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 20;
        }

        public ConditionalExpressionContext() {
        }

        public void copyFrom(ConditionalExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class AssignmentContext
    extends ParserRuleContext {
        public Token target;
        public ExpressionContext source;

        public AssignmentOperatorContext assignmentOperator() {
            return this.getRuleContext(AssignmentOperatorContext.class, 0);
        }

        public TerminalNode ID() {
            return this.getToken(49, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public AssignmentContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 18;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterAssignment(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitAssignment(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitAssignment(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class AssignmentOperatorContext
    extends ParserRuleContext {
        public TerminalNode ASSIGN() {
            return this.getToken(9, 0);
        }

        public TerminalNode POWER_ASSIGN() {
            return this.getToken(16, 0);
        }

        public TerminalNode TIMES_ASSIGN() {
            return this.getToken(13, 0);
        }

        public TerminalNode DIVIDE_ASSIGN() {
            return this.getToken(14, 0);
        }

        public TerminalNode REMAINDER_ASSIGN() {
            return this.getToken(15, 0);
        }

        public TerminalNode PLUS_ASSIGN() {
            return this.getToken(11, 0);
        }

        public TerminalNode MINUS_ASSIGN() {
            return this.getToken(12, 0);
        }

        public AssignmentOperatorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 19;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterAssignmentOperator(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitAssignmentOperator(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitAssignmentOperator(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class CEFallthroughContext
    extends ConditionalExpressionContext {
        public ConditionalOrExpressionContext conditionalOrExpression() {
            return this.getRuleContext(ConditionalOrExpressionContext.class, 0);
        }

        public CEFallthroughContext(ConditionalExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterCEFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitCEFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitCEFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ConditionalOrExpressionContext
    extends ParserRuleContext {
        public ConditionalOrExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 21;
        }

        public ConditionalOrExpressionContext() {
        }

        public void copyFrom(ConditionalOrExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class TernaryExprContext
    extends ConditionalExpressionContext {
        public ConditionalOrExpressionContext condition;
        public ExpressionContext trueBranch;
        public ConditionalExpressionContext falseBranch;

        public TerminalNode QUESTION_MARK() {
            return this.getToken(34, 0);
        }

        public TerminalNode COLON() {
            return this.getToken(35, 0);
        }

        public ConditionalOrExpressionContext conditionalOrExpression() {
            return this.getRuleContext(ConditionalOrExpressionContext.class, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public ConditionalExpressionContext conditionalExpression() {
            return this.getRuleContext(ConditionalExpressionContext.class, 0);
        }

        public TernaryExprContext(ConditionalExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterTernaryExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitTernaryExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitTernaryExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class COFallthroughContext
    extends ConditionalOrExpressionContext {
        public ConditionalAndExpressionContext conditionalAndExpression() {
            return this.getRuleContext(ConditionalAndExpressionContext.class, 0);
        }

        public COFallthroughContext(ConditionalOrExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterCOFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitCOFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitCOFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ConditionalAndExpressionContext
    extends ParserRuleContext {
        public ConditionalAndExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 22;
        }

        public ConditionalAndExpressionContext() {
        }

        public void copyFrom(ConditionalAndExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class ConditionalOrExprContext
    extends ConditionalOrExpressionContext {
        public ConditionalOrExpressionContext left;
        public ConditionalAndExpressionContext right;

        public TerminalNode OR_SC() {
            return this.getToken(25, 0);
        }

        public ConditionalOrExpressionContext conditionalOrExpression() {
            return this.getRuleContext(ConditionalOrExpressionContext.class, 0);
        }

        public ConditionalAndExpressionContext conditionalAndExpression() {
            return this.getRuleContext(ConditionalAndExpressionContext.class, 0);
        }

        public ConditionalOrExprContext(ConditionalOrExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterConditionalOrExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitConditionalOrExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitConditionalOrExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class CAFallthroughContext
    extends ConditionalAndExpressionContext {
        public EqualityExpressionContext equalityExpression() {
            return this.getRuleContext(EqualityExpressionContext.class, 0);
        }

        public CAFallthroughContext(ConditionalAndExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterCAFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitCAFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitCAFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class EqualityExpressionContext
    extends ParserRuleContext {
        public EqualityExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 23;
        }

        public EqualityExpressionContext() {
        }

        public void copyFrom(EqualityExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class ConditionalAndExprContext
    extends ConditionalAndExpressionContext {
        public ConditionalAndExpressionContext left;
        public EqualityExpressionContext right;

        public TerminalNode AND_SC() {
            return this.getToken(24, 0);
        }

        public ConditionalAndExpressionContext conditionalAndExpression() {
            return this.getRuleContext(ConditionalAndExpressionContext.class, 0);
        }

        public EqualityExpressionContext equalityExpression() {
            return this.getRuleContext(EqualityExpressionContext.class, 0);
        }

        public ConditionalAndExprContext(ConditionalAndExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterConditionalAndExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitConditionalAndExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitConditionalAndExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class EqFallthroughContext
    extends EqualityExpressionContext {
        public RelationalExpressionContext relationalExpression() {
            return this.getRuleContext(RelationalExpressionContext.class, 0);
        }

        public EqFallthroughContext(EqualityExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterEqFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitEqFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitEqFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class RelationalExpressionContext
    extends ParserRuleContext {
        public RelationalExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 24;
        }

        public RelationalExpressionContext() {
        }

        public void copyFrom(RelationalExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class EqualityExprContext
    extends EqualityExpressionContext {
        public EqualityExpressionContext left;
        public Token op;
        public RelationalExpressionContext right;

        public EqualityExpressionContext equalityExpression() {
            return this.getRuleContext(EqualityExpressionContext.class, 0);
        }

        public RelationalExpressionContext relationalExpression() {
            return this.getRuleContext(RelationalExpressionContext.class, 0);
        }

        public TerminalNode EQUAL() {
            return this.getToken(17, 0);
        }

        public TerminalNode NOT_EQUAL() {
            return this.getToken(18, 0);
        }

        public TerminalNode NEAR() {
            return this.getToken(19, 0);
        }

        public EqualityExprContext(EqualityExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterEqualityExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitEqualityExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitEqualityExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ReFallthroughContext
    extends RelationalExpressionContext {
        public ShiftExpressionContext shiftExpression() {
            return this.getRuleContext(ShiftExpressionContext.class, 0);
        }

        public ReFallthroughContext(RelationalExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterReFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitReFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitReFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ShiftExpressionContext
    extends ParserRuleContext {
        public ShiftExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 25;
        }

        public ShiftExpressionContext() {
        }

        public void copyFrom(ShiftExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class RelationalExprContext
    extends RelationalExpressionContext {
        public RelationalExpressionContext left;
        public Token op;
        public ShiftExpressionContext right;

        public RelationalExpressionContext relationalExpression() {
            return this.getRuleContext(RelationalExpressionContext.class, 0);
        }

        public ShiftExpressionContext shiftExpression() {
            return this.getRuleContext(ShiftExpressionContext.class, 0);
        }

        public TerminalNode LESS_THAN() {
            return this.getToken(20, 0);
        }

        public TerminalNode GREATER_THAN() {
            return this.getToken(22, 0);
        }

        public TerminalNode LESS_THAN_OR_EQUAL() {
            return this.getToken(21, 0);
        }

        public TerminalNode GREATER_THAN_OR_EQUAL() {
            return this.getToken(23, 0);
        }

        public RelationalExprContext(RelationalExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterRelationalExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitRelationalExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitRelationalExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ShFallthroughContext
    extends ShiftExpressionContext {
        public AdditiveExpressionContext additiveExpression() {
            return this.getRuleContext(AdditiveExpressionContext.class, 0);
        }

        public ShFallthroughContext(ShiftExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterShFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitShFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitShFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class AdditiveExpressionContext
    extends ParserRuleContext {
        public AdditiveExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 26;
        }

        public AdditiveExpressionContext() {
        }

        public void copyFrom(AdditiveExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class ShiftExprContext
    extends ShiftExpressionContext {
        public ShiftExpressionContext left;
        public Token op;
        public AdditiveExpressionContext right;

        public ShiftExpressionContext shiftExpression() {
            return this.getRuleContext(ShiftExpressionContext.class, 0);
        }

        public AdditiveExpressionContext additiveExpression() {
            return this.getRuleContext(AdditiveExpressionContext.class, 0);
        }

        public TerminalNode LEFT_SHIFT() {
            return this.getToken(7, 0);
        }

        public TerminalNode RIGHT_SHIFT() {
            return this.getToken(8, 0);
        }

        public ShiftExprContext(ShiftExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterShiftExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitShiftExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitShiftExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class AdFallthroughContext
    extends AdditiveExpressionContext {
        public MultiplicativeExpressionContext multiplicativeExpression() {
            return this.getRuleContext(MultiplicativeExpressionContext.class, 0);
        }

        public AdFallthroughContext(AdditiveExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterAdFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitAdFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitAdFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class MultiplicativeExpressionContext
    extends ParserRuleContext {
        public MultiplicativeExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 27;
        }

        public MultiplicativeExpressionContext() {
        }

        public void copyFrom(MultiplicativeExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class AddExprContext
    extends AdditiveExpressionContext {
        public AdditiveExpressionContext left;
        public Token op;
        public MultiplicativeExpressionContext right;

        public AdditiveExpressionContext additiveExpression() {
            return this.getRuleContext(AdditiveExpressionContext.class, 0);
        }

        public MultiplicativeExpressionContext multiplicativeExpression() {
            return this.getRuleContext(MultiplicativeExpressionContext.class, 0);
        }

        public TerminalNode PLUS() {
            return this.getToken(1, 0);
        }

        public TerminalNode MINUS() {
            return this.getToken(2, 0);
        }

        public AddExprContext(AdditiveExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterAddExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitAddExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitAddExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class MuFallthroughContext
    extends MultiplicativeExpressionContext {
        public PowerExpressionContext powerExpression() {
            return this.getRuleContext(PowerExpressionContext.class, 0);
        }

        public MuFallthroughContext(MultiplicativeExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterMuFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitMuFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitMuFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PowerExpressionContext
    extends ParserRuleContext {
        public PowerExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 28;
        }

        public PowerExpressionContext() {
        }

        public void copyFrom(PowerExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class MultiplicativeExprContext
    extends MultiplicativeExpressionContext {
        public MultiplicativeExpressionContext left;
        public Token op;
        public PowerExpressionContext right;

        public MultiplicativeExpressionContext multiplicativeExpression() {
            return this.getRuleContext(MultiplicativeExpressionContext.class, 0);
        }

        public PowerExpressionContext powerExpression() {
            return this.getRuleContext(PowerExpressionContext.class, 0);
        }

        public TerminalNode TIMES() {
            return this.getToken(3, 0);
        }

        public TerminalNode DIVIDE() {
            return this.getToken(4, 0);
        }

        public TerminalNode REMAINDER() {
            return this.getToken(5, 0);
        }

        public MultiplicativeExprContext(MultiplicativeExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterMultiplicativeExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitMultiplicativeExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitMultiplicativeExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PwFallthroughContext
    extends PowerExpressionContext {
        public UnaryExpressionContext unaryExpression() {
            return this.getRuleContext(UnaryExpressionContext.class, 0);
        }

        public PwFallthroughContext(PowerExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterPwFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitPwFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitPwFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class UnaryExpressionContext
    extends ParserRuleContext {
        public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 29;
        }

        public UnaryExpressionContext() {
        }

        public void copyFrom(UnaryExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class PowerExprContext
    extends PowerExpressionContext {
        public PowerExpressionContext left;
        public UnaryExpressionContext right;

        public TerminalNode POWER() {
            return this.getToken(6, 0);
        }

        public PowerExpressionContext powerExpression() {
            return this.getRuleContext(PowerExpressionContext.class, 0);
        }

        public UnaryExpressionContext unaryExpression() {
            return this.getRuleContext(UnaryExpressionContext.class, 0);
        }

        public PowerExprContext(PowerExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterPowerExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitPowerExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitPowerExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PreCrementExprContext
    extends UnaryExpressionContext {
        public Token op;
        public Token target;

        public TerminalNode ID() {
            return this.getToken(49, 0);
        }

        public TerminalNode INCREMENT() {
            return this.getToken(26, 0);
        }

        public TerminalNode DECREMENT() {
            return this.getToken(27, 0);
        }

        public PreCrementExprContext(UnaryExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterPreCrementExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitPreCrementExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitPreCrementExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PlusMinusExprContext
    extends UnaryExpressionContext {
        public Token op;
        public UnaryExpressionContext expr;

        public UnaryExpressionContext unaryExpression() {
            return this.getRuleContext(UnaryExpressionContext.class, 0);
        }

        public TerminalNode PLUS() {
            return this.getToken(1, 0);
        }

        public TerminalNode MINUS() {
            return this.getToken(2, 0);
        }

        public PlusMinusExprContext(UnaryExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterPlusMinusExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitPlusMinusExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitPlusMinusExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class UaFallthroughContext
    extends UnaryExpressionContext {
        public PostfixExpressionContext postfixExpression() {
            return this.getRuleContext(PostfixExpressionContext.class, 0);
        }

        public UaFallthroughContext(UnaryExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterUaFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitUaFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitUaFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PostfixExpressionContext
    extends ParserRuleContext {
        public PostfixExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 30;
        }

        public PostfixExpressionContext() {
        }

        public void copyFrom(PostfixExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class ComplementExprContext
    extends UnaryExpressionContext {
        public UnaryExpressionContext expr;

        public TerminalNode COMPLEMENT() {
            return this.getToken(10, 0);
        }

        public UnaryExpressionContext unaryExpression() {
            return this.getRuleContext(UnaryExpressionContext.class, 0);
        }

        public ComplementExprContext(UnaryExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterComplementExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitComplementExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitComplementExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class NotExprContext
    extends UnaryExpressionContext {
        public UnaryExpressionContext expr;

        public TerminalNode EXCLAMATION_MARK() {
            return this.getToken(36, 0);
        }

        public UnaryExpressionContext unaryExpression() {
            return this.getRuleContext(UnaryExpressionContext.class, 0);
        }

        public NotExprContext(UnaryExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterNotExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitNotExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitNotExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PoFallthroughContext
    extends PostfixExpressionContext {
        public UnprioritizedExpressionContext unprioritizedExpression() {
            return this.getRuleContext(UnprioritizedExpressionContext.class, 0);
        }

        public PoFallthroughContext(PostfixExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterPoFallthrough(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitPoFallthrough(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitPoFallthrough(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class UnprioritizedExpressionContext
    extends ParserRuleContext {
        public UnprioritizedExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 31;
        }

        public UnprioritizedExpressionContext() {
        }

        public void copyFrom(UnprioritizedExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class PostCrementExprContext
    extends PostfixExpressionContext {
        public Token target;
        public Token op;

        public TerminalNode ID() {
            return this.getToken(49, 0);
        }

        public TerminalNode INCREMENT() {
            return this.getToken(26, 0);
        }

        public TerminalNode DECREMENT() {
            return this.getToken(27, 0);
        }

        public PostCrementExprContext(PostfixExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterPostCrementExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitPostCrementExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitPostCrementExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PostfixExprContext
    extends PostfixExpressionContext {
        public PostfixExpressionContext expr;
        public Token op;

        public PostfixExpressionContext postfixExpression() {
            return this.getRuleContext(PostfixExpressionContext.class, 0);
        }

        public TerminalNode EXCLAMATION_MARK() {
            return this.getToken(36, 0);
        }

        public PostfixExprContext(PostfixExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterPostfixExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitPostfixExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitPostfixExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class FunctionCallExprContext
    extends UnprioritizedExpressionContext {
        public FunctionCallContext functionCall() {
            return this.getRuleContext(FunctionCallContext.class, 0);
        }

        public FunctionCallExprContext(UnprioritizedExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterFunctionCallExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitFunctionCallExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitFunctionCallExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class FunctionCallContext
    extends ParserRuleContext {
        public Token name;
        public ExpressionContext expression;
        public List<ExpressionContext> args = new ArrayList<ExpressionContext>();

        public TerminalNode OPEN_PAREN() {
            return this.getToken(29, 0);
        }

        public TerminalNode CLOSE_PAREN() {
            return this.getToken(30, 0);
        }

        public TerminalNode ID() {
            return this.getToken(49, 0);
        }

        public List<ExpressionContext> expression() {
            return this.getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return this.getRuleContext(ExpressionContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(28);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(28, i);
        }

        public FunctionCallContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 33;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterFunctionCall(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitFunctionCall(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitFunctionCall(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ConstantExprContext
    extends UnprioritizedExpressionContext {
        public ConstantExpressionContext constantExpression() {
            return this.getRuleContext(ConstantExpressionContext.class, 0);
        }

        public ConstantExprContext(UnprioritizedExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterConstantExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitConstantExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitConstantExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class IdExprContext
    extends UnprioritizedExpressionContext {
        public Token source;

        public TerminalNode ID() {
            return this.getToken(49, 0);
        }

        public IdExprContext(UnprioritizedExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterIdExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitIdExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitIdExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class WrappedExprContext
    extends UnprioritizedExpressionContext {
        public TerminalNode OPEN_PAREN() {
            return this.getToken(29, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode CLOSE_PAREN() {
            return this.getToken(30, 0);
        }

        public WrappedExprContext(UnprioritizedExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).enterWrappedExpr(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ExpressionListener) {
                ((ExpressionListener)listener).exitWrappedExpr(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ExpressionVisitor) {
                return ((ExpressionVisitor)visitor).visitWrappedExpr(this);
            }
            return visitor.visitChildren(this);
        }
    }
}

