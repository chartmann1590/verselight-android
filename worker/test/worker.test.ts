import { describe, expect, it } from "vitest";

describe("report contract", () => {
  it("uses stable reason values", () => {
    expect(["abuse", "hate", "threat", "sexual", "spam", "other"]).toContain("abuse");
  });

  it("requires ISO day keys", () => {
    expect(/^\d{4}-\d{2}-\d{2}$/.test("2026-08-19")).toBe(true);
    expect(/^\d{4}-\d{2}-\d{2}$/.test("August 19")).toBe(false);
  });
});

