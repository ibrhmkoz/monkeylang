package io.github.ibrhmkoz.monkeylang.parser.basic;

import io.github.ibrhmkoz.lib.result.Err;
import io.github.ibrhmkoz.lib.result.Ok;
import io.github.ibrhmkoz.lib.result.Result;
import io.github.ibrhmkoz.monkeylang.ast.InfixOperator;
import io.github.ibrhmkoz.monkeylang.ast.Node;
import io.github.ibrhmkoz.monkeylang.ast.PrefixOperator;
import io.github.ibrhmkoz.monkeylang.parser.Parser;
import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.Tokenizer;

import java.util.ArrayList;
import java.util.List;

record BindingPower(int left, int right) {

    static final int PREFIX_RIGHT = 10;

    static BindingPower of(Token.InfixOp op) {
        return switch (op) {
            case Token.Eq _, Token.NotEq _ -> new BindingPower(2, 3);
            case Token.LessThan _, Token.GreaterThan _ -> new BindingPower(4, 5);
            case Token.Plus _, Token.Minus _ -> new BindingPower(6, 7);
            case Token.Asterisk _, Token.Slash _ -> new BindingPower(8, 9);
        };
    }
}

public class BasicParser implements Parser {

    private final Tokenizer tokenizer;
    private Token curToken;
    private Token peekToken;

    public BasicParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        advance();
        advance();
    }

    @Override
    public Result<Node.Program, List<String>> parse() {
        var statements = new ArrayList<Node.Statement>();
        var errors = new ArrayList<String>();
        while (!(curToken instanceof Token.Eof)) {
            switch (parseStatement()) {
                case Ok<Node.Statement, String>(var stmt) -> statements.add(stmt);
                case Err<Node.Statement, String>(var error) -> errors.add(error);
            }
            advance();
        }
        if (!errors.isEmpty()) {
            return Result.err(errors);
        }
        return Result.ok(new Node.Program(statements));
    }

    private Result<Node.Statement, String> parseStatement() {
        return switch (curToken) {
            case Token.Let _ -> parseLetStatement();
            case Token.Return _ -> parseReturnStatement();
            default -> parseExpressionStatement();
        };
    }

    private Result<Node.Statement, String> parseExpressionStatement() {
        return switch (parseExpression(0)) {
            case Ok<Node.Expression, String>(var expr) -> {
                if (peekToken instanceof Token.Semicolon) {
                    advance();
                }
                yield Result.ok(new Node.Statement.Expr(expr));
            }
            case Err<Node.Expression, String>(var error) -> Result.err(error);
        };
    }

    private Result<Node.Expression, String> parseExpression(int bp) {
        var left = parsePrefix();

        while (left instanceof Ok<Node.Expression, String>(var expr)
            && peekToken instanceof Token.InfixOp op
            && bp < BindingPower.of(op).left()) {
            advance();
            left = parseInfix(expr);
        }

        return left;
    }

    private Result<Node.Expression, String> parsePrefix() {
        return switch (curToken) {
            case Token.Ident(var name) -> Result.ok(new Node.Expression.Identifier(name));
            case Token.Int(var value) -> Result.ok(new Node.Expression.IntegerLiteral(value));
            case Token.Bool(var value) -> Result.ok(new Node.Expression.BooleanLiteral(value));
            case Token.Bang _, Token.Minus _ -> parsePrefixExpression();
            case Token.LParen _ -> parseGroupedExpression();
            default -> Result.err("no prefix parser for: " + curToken);
        };
    }

    private Result<Node.Expression, String> parsePrefixExpression() {
        var operator = switch (curToken) {
            case Token.Minus _ -> PrefixOperator.NEGATE;
            case Token.Bang _ -> PrefixOperator.NOT;
            default -> throw new IllegalStateException("unexpected prefix token: " + curToken);
        };
        advance();
        return switch (parseExpression(BindingPower.PREFIX_RIGHT)) {
            case Ok<Node.Expression, String>(var right) -> Result.ok(new Node.Expression.Prefix(operator, right));
            case Err<Node.Expression, String> err -> err;
        };
    }

    private Result<Node.Expression, String> parseGroupedExpression() {
        advance();
        var expr = parseExpression(0);
        if (!(peekToken instanceof Token.RParen)) {
            return Result.err("expected ), got: " + peekToken);
        }
        advance();
        return expr;
    }

    private Result<Node.Expression, String> parseInfix(Node.Expression left) {
        if (!(curToken instanceof Token.InfixOp op)) {
            throw new IllegalStateException("unexpected infix token: " + curToken);
        }
        var operator = switch (op) {
            case Token.Plus _ -> InfixOperator.ADD;
            case Token.Minus _ -> InfixOperator.SUBTRACT;
            case Token.Asterisk _ -> InfixOperator.MULTIPLY;
            case Token.Slash _ -> InfixOperator.DIVIDE;
            case Token.Eq _ -> InfixOperator.EQUAL;
            case Token.NotEq _ -> InfixOperator.NOT_EQUAL;
            case Token.LessThan _ -> InfixOperator.LESS_THAN;
            case Token.GreaterThan _ -> InfixOperator.GREATER_THAN;
        };
        var rbp = BindingPower.of(op).right();
        advance();
        return switch (parseExpression(rbp)) {
            case Ok<Node.Expression, String>(var right) -> Result.ok(new Node.Expression.Infix(left, operator, right));
            case Err<Node.Expression, String> err -> err;
        };
    }

    private Result<Node.Statement, String> parseLetStatement() {
        if (!(peekToken instanceof Token.Ident(String name))) {
            skipToSemicolon();
            return Result.err("expected identifier, got: " + peekToken);
        }
        advance();

        if (!(peekToken instanceof Token.Assign)) {
            skipToSemicolon();
            return Result.err("expected '=', got: " + peekToken);
        }

        advance();
        skipToSemicolon();

        return Result.ok(new Node.Statement.Let(name, null));
    }

    private Result<Node.Statement, String> parseReturnStatement() {
        advance();
        skipToSemicolon();
        return Result.ok(new Node.Statement.Return(null));
    }

    private void skipToSemicolon() {
        while (!(curToken instanceof Token.Semicolon) && !(curToken instanceof Token.Eof)) {
            advance();
        }
    }

    private void advance() {
        curToken = peekToken;
        peekToken = tokenizer.nextToken();
    }
}
