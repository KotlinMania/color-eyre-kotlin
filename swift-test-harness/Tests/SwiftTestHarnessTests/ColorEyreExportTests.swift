import Testing
import ColorEyre

@Suite("ColorEyre Swift Export Tests")
struct ColorEyreExportTests {
    @Test("Swift module imports and basic types are reachable")
    func swiftModuleLoads() throws {
        #expect(Bool(true))
    }
}
