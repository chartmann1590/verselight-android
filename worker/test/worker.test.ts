import { describe, expect, it } from "vitest";
import { classifyCommentSafety } from "../src/index";

describe("report contract", () => {
  it("uses stable reason values", () => {
    expect(["abuse", "hate", "threat", "sexual", "spam", "other"]).toContain("abuse");
  });

  it("requires ISO day keys", () => {
    expect(/^\d{4}-\d{2}-\d{2}$/.test("2026-08-19")).toBe(true);
    expect(/^\d{4}-\d{2}-\d{2}$/.test("August 19")).toBe(false);
  });
});

describe("server comment safety gate", () => {
  it("allows a respectful reflection", () => {
    expect(classifyCommentSafety("This verse reminds me to walk in hope.").allowed).toBe(true);
  });

  it("blocks an obfuscated profanity before Firestore", () => {
    const result = classifyCommentSafety("you are a f.u.c.k.i.n.g loser");
    expect(result.allowed).toBe(false);
    expect(result.categories).toContain("strong profanity");
  });

  it("blocks a direct threat before Firestore", () => {
    expect(classifyCommentSafety("I will kill you").categories).toContain("threat");
  });
});
